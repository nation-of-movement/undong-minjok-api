package com.undongminjok.api.templates.service;

import com.undongminjok.api.equipments.repository.EquipmentRepository;
import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.global.dto.PageRequestDto;
import com.undongminjok.api.global.dto.PageResponseDto;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.template_storage.domain.TemplateStorage;
import com.undongminjok.api.template_storage.repository.TemplateStorageRepository;
import com.undongminjok.api.templates.TemplateErrorCode;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.domain.TemplateSortType;
import com.undongminjok.api.templates.domain.TemplateStatus; // ⭐ 추가
import com.undongminjok.api.templates.dto.request.TemplateCreateRequestDTO;
import com.undongminjok.api.templates.dto.response.TemplateDetailResponseDTO;
import com.undongminjok.api.templates.dto.response.TemplateDetailResponseDTO.TemplateDayDTO;
import com.undongminjok.api.templates.dto.response.TemplateListResponseDTO;
import com.undongminjok.api.templates.dto.TemplateSalesHistoryDTO;
import com.undongminjok.api.templates.dto.request.TemplateUpdateRequestDTO;
import com.undongminjok.api.templates.repository.TemplateRecommendRepository;
import com.undongminjok.api.templates.repository.TemplateRepository;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.dto.UserProfileResponse;
import com.undongminjok.api.user.repository.UserRepository;
import com.undongminjok.api.user.service.provider.UserProviderService;
import com.undongminjok.api.workoutplan.workoutPlan.WorkoutPlan;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExercise;
import com.undongminjok.api.workoutplan.workoutPlanExercise.WorkoutPlanExerciseDTO;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateRecommendRepository recommendRepository;
    private final TemplateStorageRepository templateStorageRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final SecurityUtil securityUtil;
    private final FileStorage fileStorage;
    private final UserProviderService userProviderService;

    //================================== 목록 조회 ===================================
    /*
     * 템플릿 전체 목록 조회
     * */
    public List<TemplateListResponseDTO> findAllTemplates() {
        //페이징 없앴습니다. 밑에 paged 메소드가 따로 있어서
        //페이징 원하시면 프론트에서 충분히 가능합니다!

        //status가 STOPPED가 아닌 것만 가져옴
        List<Template> templates =
                templateRepository.findAllByStatusNotOrderByCreatedAtDesc(TemplateStatus.STOPPED);

        return templates.stream()
                .map(TemplateListResponseDTO::from)
                .toList();
    }

    /*
     * 템플릿 전체 목록 페이징 조회
     * */
    public PageResponseDto<TemplateListResponseDTO> getTemplatePage(
            PageRequestDto req,
            String name,
            TemplateSortType sortType
    ) {

        Pageable pageable = createSortPageable(req, sortType);

        Page<Template> page;

        // 검색이 있는 경우
        if (name != null && !name.isBlank()) {
            page = templateRepository.findByNameContainingAndStatusNot(
                    name,
                    TemplateStatus.STOPPED,
                    pageable
            );
        } else {
            page = templateRepository.findByStatusNot(
                    TemplateStatus.STOPPED,
                    pageable
            );
        }

        Page<TemplateListResponseDTO> dtoPage = page.map(TemplateListResponseDTO::from);

        return PageResponseDto.from(dtoPage);
    }

    //정렬기준 생성 메서드
    private Pageable createSortPageable(PageRequestDto req, TemplateSortType sortType) {

        Sort sort = switch (sortType) {
            case SALES -> Sort.by(
                    Sort.Order.desc("salesCount"),
                    Sort.Order.desc("createdAt")      // 동점자: 최신순
            );
            case RECOMMEND -> Sort.by(
                    Sort.Order.desc("recommendCount"),
                    Sort.Order.desc("createdAt")      // 동점자: 최신순
            );
            default -> Sort.by(
                    Sort.Order.desc("createdAt")      // 기본: 최신순
            );
        };

        return PageRequest.of(req.getPage(), req.getSize(), sort);
    }

    //================================== 상세 조회 ===================================
    /*
     * 템플릿 상세조회
     * */
    public TemplateDetailResponseDTO getTemplateDetail(Long templateId) {

        //템플릿 기본 정보 조회
        Template template = findTemplateOrThrow(templateId);

        //현재 로그인한 유저가 이 템플릿을 추천했는지 여부
        boolean recommended = isTemplateRecommendedByCurrentUser(template);

        // 템플릿에 연결된 운동들을 day기준으로 그룹핑
        List<TemplateDetailResponseDTO.TemplateDayDTO> days = buildTemplateDays(template);

      Long loginUserId = securityUtil.getCurrentUserIdOrNull();

      boolean isMine = false;
      if (loginUserId != null) {
        isMine = template.getUser().getUserId().equals(loginUserId);
      }

      UserProfileResponse userProfileResponse = userProviderService.getUserProfile(template.getUser().getUserId());

        //최종 응답 DTO로 변환
        return TemplateDetailResponseDTO.of(template, recommended, days, isMine, userProfileResponse);
    }

    private Template findTemplateOrThrow(Long templateId) {
        return templateRepository.findDetailById(templateId)
                .orElseThrow(() -> new BusinessException(TemplateErrorCode.TEMPLATE_NOT_FOUND));
    }

    private boolean isTemplateRecommendedByCurrentUser(Template template) {

        Long loginUserId = securityUtil.getCurrentUserIdOrNull();
        if (loginUserId == null) {
            return false;
        }

        User loginUser = userRepository.findById(loginUserId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        return recommendRepository
                .findByUserAndTemplate(loginUser, template)
                .isPresent();
    }

    private List<TemplateDayDTO> buildTemplateDays(Template template) {

        //템플릿에 연결된 WorkoutPlan 없을 수도 있으므로 null 방어
        List<WorkoutPlanExercise> exercises =
                template.getWorkoutPlan() != null
                        ? template.getWorkoutPlan().getExercises()
                        : List.of();

        //1. day, orderIndex 기준으로 정렬
        //2. WorkoutPlanExerciseDTO로 변환
        //3. day별로 그룹핑
        Map<Integer, List<WorkoutPlanExerciseDTO>> grouped =
                exercises.stream()
                        .sorted(Comparator
                                .comparing(WorkoutPlanExercise::getDay)
                                .thenComparing(WorkoutPlanExercise::getOrderIndex, Comparator.nullsLast(Integer::compareTo)))
                        .map(WorkoutPlanExerciseDTO::from)
                        .collect(Collectors.groupingBy(
                                WorkoutPlanExerciseDTO::getDay,
                                TreeMap::new,
                                Collectors.toList()
                        ));

        //day -> TemplateDayDTO로 변환
        return grouped.entrySet().stream()
                .map(e -> TemplateDetailResponseDTO.TemplateDayDTO.builder()
                        .day(e.getKey())
                        .exercises(e.getValue())
                        .build())
                .toList();
    }

    //================================== 템플릿 만들기 ===================================
    /*
     * 템플릿 생성
     * */
    @Transactional
    public void createTemplate(
            TemplateCreateRequestDTO req,
            MultipartFile thumbnail,
            MultipartFile detailImage
    ) {

        Long userId = SecurityUtil.getLoginUserInfo().getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        //템플릿 엔티티 생성
        Template template = createTemplateEntity(req, user);

        //WorkoutPlan 생성
        WorkoutPlan plan = createWorkoutPlan(template);

        //Exercise 생성
        createExercises(req, plan);

        //이미지 업로드
        uploadImages(template, thumbnail, detailImage);

        templateRepository.save(template);
      TemplateStorage storage = TemplateStorage.builder()
          .user(user)
          .template(template)
          .deleted(false)
          .build();

      templateStorageRepository.save(storage);


    }

    private Template createTemplateEntity(TemplateCreateRequestDTO req, User user) {
        return Template.builder()
                .name(req.getName())
                .content(req.getContent())
                .price(req.getPrice())
                .status(req.getStatus())
                .user(user)
                .build();
    }

    private WorkoutPlan createWorkoutPlan(Template template) {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setTemplate(template);
        template.setWorkoutPlan(plan);
        return plan;
    }

    private void createExercises(TemplateCreateRequestDTO req, WorkoutPlan plan) {

        if (req.getExercises() == null) return;

        req.getExercises().forEach(exDto -> {
            Equipment eq = exDto.getEquipmentId() != null
                    ? equipmentRepository.getReferenceById(exDto.getEquipmentId())
                    : null;

            WorkoutPlanExercise ex = WorkoutPlanExercise.builder()
                    .day(exDto.getDay())
                    .name(exDto.getName())
                    .part(exDto.getPart())
                    .reps(exDto.getReps())
                    .weight(exDto.getWeight())
                    .duration(exDto.getDuration())
                    .orderIndex(exDto.getOrderIndex())
                    .equipment(eq)
                    .build();

            plan.addExercise(ex);
        });
    }

    private void uploadImages(Template template, MultipartFile thumbnail, MultipartFile detailImage) {

        if (thumbnail != null && !thumbnail.isEmpty()) {
            String path = fileStorage.store(thumbnail, ImageCategory.THUMBNAIL);
            template.updateThumbnail(path);
        }

        if (detailImage != null && !detailImage.isEmpty()) {
            String path = fileStorage.store(detailImage, ImageCategory.DETAIL);
            template.updateTemplateImage(path);
        }
    }


    /*
     * 템플릿 수정
     * */
    @Transactional
    public void updateTemplate(Long templateId, TemplateUpdateRequestDTO req) {
      Long loginUserId = SecurityUtil.getLoginUserInfo().getUserId();

      // 1. 템플릿 + 플랜 + 운동까지 한 번에 가져오는게 제일 좋음
      Template template = templateRepository.findDetailById(templateId)
          .orElseThrow(() -> new BusinessException(TemplateErrorCode.TEMPLATE_NOT_FOUND));

      // 2. 권한 체크
      if (!template.getUser().getUserId().equals(loginUserId)) {
        throw new BusinessException(TemplateErrorCode.TEMPLATE_UPDATE_FORBIDDEN);
      }

      // 3. 템플릿 기본 정보 수정 (content, price, status만)
      template.update(req.getContent(), req.getPrice());

      // 4. 운동 수정 로직
      WorkoutPlan plan = template.getWorkoutPlan();
      if (plan == null) {
        // 혹시라도 null인 경우 방어
        plan = new WorkoutPlan();
        plan.setTemplate(template);
        template.setWorkoutPlan(plan);
      }

      // 현재 운동들 Map으로 캐싱 (id -> 엔티티)
      Map<Long, WorkoutPlanExercise> currentMap = plan.getExercises().stream()
          .filter(e -> e.getId() != null)
          .collect(Collectors.toMap(WorkoutPlanExercise::getId, e -> e));

      if (req.getExercises() == null) {
        return; // 운동 수정 안하면 여기서 끝
      }

      for (TemplateUpdateRequestDTO.ExerciseUpdateDTO eDto : req.getExercises()) {

        // id 없는데 deleted=true → 무시
        if (eDto.getExerciseId() == null && eDto.isDeleted()) {
          continue;
        }

        // 4-1. 새 운동 추가
        if (eDto.getExerciseId() == null) {
          Equipment eq = eDto.getEquipmentId() != null
              ? equipmentRepository.getReferenceById(eDto.getEquipmentId())
              : null;

          WorkoutPlanExercise ex = WorkoutPlanExercise.builder()
              .day(eDto.getDay())
              .name(eDto.getName())
              .part(eDto.getPart())
              .reps(eDto.getReps())
              .weight(eDto.getWeight())
              .duration(eDto.getDuration())
              .orderIndex(eDto.getOrderIndex())
              .equipment(eq)
              .build();

          plan.addExercise(ex);   // 연관관계 편의 메서드
          continue;
        }

        // 4-2. 기존 운동 찾기
        WorkoutPlanExercise exist = currentMap.get(eDto.getExerciseId());
        if (exist == null) {
          throw new BusinessException(TemplateErrorCode.TEMPLATE_EXERCISE_NOT_FOUND);
        }

        // 4-3. 삭제 플래그 → 제거
        if (eDto.isDeleted()) {
          plan.removeExercise(exist); // orphanRemoval = true면 delete까지 자동
          continue;
        }

        // 4-4. 값 수정
        Equipment eq = eDto.getEquipmentId() != null
            ? equipmentRepository.getReferenceById(eDto.getEquipmentId())
            : null;

        exist.update(
            eDto.getDay(),
            eDto.getName(),
            eDto.getPart(),
            eDto.getReps(),
            eDto.getWeight(),
            eDto.getDuration(),
            eDto.getOrderIndex(),
            eq
        );
      }
    }

    // 템플릿 삭제 (Hard + Soft Delete 적용)
    @Transactional
    public void deleteTemplate(Long templateId) {

        Long loginUserId = SecurityUtil.getLoginUserInfo().getUserId();

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 템플릿 없음"));

        //  다른 사람이 만든 템플릿 삭제 불가
        if (!template.getUser().getUserId().equals(loginUserId)) {
            throw new IllegalArgumentException("템플릿 삭제 권한 없음");
        }

        //  이미 판매된 템플릿은 삭제 금지 → Soft Delete
        if (template.getSalesCount() >= 1) {
            template.softDelete();     //  상태만 STOPPED 로 변경
            return;
        }

        //  Hard delete (판매된 적 없는 템플릿만)
        if (template.getThumbnailImage() != null)
            fileStorage.deleteQuietly(template.getThumbnailImage());

        if (template.getTemplateImage() != null)
            fileStorage.deleteQuietly(template.getTemplateImage());

        templateRepository.delete(template);
    }

    // 내 판매내역 조회
    @Transactional(readOnly = true)
    public List<TemplateSalesHistoryDTO> getMySalesHistory(Long userId) {
        return templateRepository.findSalesHistoryByUser(userId);
    }

}
