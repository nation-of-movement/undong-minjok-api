package com.undongminjok.api.templates.service;

import com.undongminjok.api.equipments.repository.EquipmentRepository;
import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.global.dto.PageRequestDto;
import com.undongminjok.api.global.dto.PageResponseDto;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.global.util.SecurityUtil;
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
import com.undongminjok.api.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final SecurityUtil securityUtil;
    private final FileStorage fileStorage;

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

        //최종 응답 DTO로 변환
        return TemplateDetailResponseDTO.of(template, recommended, days);
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


    // 템플릿 생성
    @Transactional
    public void createTemplate(
            TemplateCreateRequestDTO req,
            MultipartFile thumbnail,
            MultipartFile detailImage
    ) {

        Long userId = securityUtil.getLoginUserInfo().getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        //  상태값 적용 (FREE / PAID / STOPPED)
        Template template = Template.builder()
                .name(req.getName())
                .content(req.getContent())
                .price(req.getPrice())
                .user(user)
                .status(req.getStatus())   //  추가됨
                .build();

        WorkoutPlan plan = new WorkoutPlan();
        plan.setTemplate(template);
        template.setWorkoutPlan(plan);

        templateRepository.save(template);

        // Exercise 생성
        if (req.getExercises() != null) {
            for (TemplateCreateRequestDTO.ExerciseCreateDTO exDto : req.getExercises()) {

                Equipment eq = null;
                if (exDto.getEquipmentId() != null) {
                    eq = equipmentRepository.getReferenceById(exDto.getEquipmentId());
                }

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
            }
        }

        templateRepository.save(template);

        if (thumbnail != null && !thumbnail.isEmpty()) {
            template.updateThumbnail(fileStorage.store(thumbnail, ImageCategory.THUMBNAIL));
        }

        if (detailImage != null && !detailImage.isEmpty()) {
            template.updateTemplateImage(fileStorage.store(detailImage, ImageCategory.DETAIL));
        }
    }

    // 템플릿 수정
    @Transactional
    public void updateTemplate(
            Long templateId,
            TemplateUpdateRequestDTO req,
            MultipartFile thumbnail,
            MultipartFile detailImage
    ) {

        Long loginUserId = securityUtil.getLoginUserInfo().getUserId();

        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("템플릿 없음"));

        if (!template.getUser().getUserId().equals(loginUserId)) {
            throw new IllegalArgumentException("템플릿 수정 권한 없음");
        }

        template.update(req.getContent(), req.getPrice());

        if (thumbnail != null && !thumbnail.isEmpty()) {
            if (template.getThumbnailImage() != null) {
                fileStorage.deleteQuietly(template.getThumbnailImage());
            }
            template.updateThumbnail(fileStorage.store(thumbnail, ImageCategory.THUMBNAIL));
        }

        if (detailImage != null && !detailImage.isEmpty()) {
            if (template.getTemplateImage() != null) {
                fileStorage.deleteQuietly(template.getTemplateImage());
            }
            template.updateTemplateImage(fileStorage.store(detailImage, ImageCategory.DETAIL));
        }
    }

    // 템플릿 삭제 (Hard + Soft Delete 적용)
    @Transactional
    public void deleteTemplate(Long templateId) {

        Long loginUserId = securityUtil.getLoginUserInfo().getUserId();

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

    // 내 구매내역조회
    @Transactional(readOnly = true)
    public List<TemplateSalesHistoryDTO> getMySalesHistory(Long userId) {
        return templateRepository.findSalesHistoryByUser(userId);
    }

}
