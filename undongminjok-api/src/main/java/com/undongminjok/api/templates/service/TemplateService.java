package com.undongminjok.api.templates.service;

import com.undongminjok.api.equipments.repository.EquipmentRepository;
import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.global.dto.PageRequestDto;
import com.undongminjok.api.global.dto.PageResponseDto;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.domain.TemplateSortType;
import com.undongminjok.api.templates.domain.TemplateStatus; // ⭐ 추가
import com.undongminjok.api.templates.dto.TemplateCreateRequestDTO;
import com.undongminjok.api.templates.dto.TemplateDetailDTO;
import com.undongminjok.api.templates.dto.TemplateDetailDTO.TemplateDayDTO;
import com.undongminjok.api.templates.dto.TemplateListDTO;
import com.undongminjok.api.templates.dto.TemplateUpdateRequestDTO;
import com.undongminjok.api.templates.repository.TemplateRecommendRepository;
import com.undongminjok.api.templates.repository.TemplateRepository;
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

  // 템플릿 전체 목록 조회
  public List<TemplateListDTO> findAllTemplates() {
    Pageable pageable = PageRequest.of(
        0,                      // 첫 페이지
        1000,                   // 충분히 큰 size (또는 원하는 값)
        Sort.by(Sort.Order.desc("createdAt"))  // 기본 정렬: 최신순
    );

    Page<Template> page = templateRepository.findByStatusNot(
        TemplateStatus.STOPPED,
        pageable
    );

    return page.getContent()
        .stream()
        .map(TemplateListDTO::from)
        .toList();
  }

  // 템플릿 이름 검색 조회
  public List<TemplateListDTO> findByTemplateName(String keyword) {
    return templateRepository.findByNameContaining(keyword)
        .stream()
        .map(TemplateListDTO::from)
        .toList();
  }

  // 템플릿 상세조회 (day 그룹핑)
  public TemplateDetailDTO getTemplateDetail(Long templateId) {

    Long loginUserId = securityUtil.getLoginUserInfo().getUserId();

    User loginUser = userRepository.findById(loginUserId)
        .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    Template template = templateRepository.findDetailById(templateId)
        .orElseThrow(() -> new IllegalArgumentException("템플릿 없음"));

    boolean recommended = recommendRepository
        .findByUserAndTemplate(loginUser, template)
        .isPresent();

    List<WorkoutPlanExercise> exercises =
        template.getWorkoutPlan() != null
            ? template.getWorkoutPlan().getExercises()
            : List.of();

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

    List<TemplateDayDTO> days = grouped.entrySet().stream()
        .map(e -> TemplateDayDTO.builder()
            .day(e.getKey())
            .exercises(e.getValue())
            .build())
        .toList();

    return TemplateDetailDTO.of(template, recommended, days);
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

  // 정렬 조회 (추천/판매/최신)
  public List<TemplateListDTO> getSortedTemplates(TemplateSortType sortType) {

    List<Template> templates = switch (sortType) {
      case RECOMMEND -> templateRepository.findAllByStatusNotOrderByRecommendCountDesc(TemplateStatus.STOPPED); // 추천순
      case SALES     -> templateRepository.findAllByStatusNotOrderBySalesCountDesc(TemplateStatus.STOPPED); //판매순
      case LATEST    -> templateRepository.findAllByStatusNotOrderByCreatedAtDesc(TemplateStatus.STOPPED);  // 최신순
      default        -> templateRepository.findAllByStatusNotOrderByCreatedAtDesc(TemplateStatus.STOPPED);  // 기본 최신순
    };

    return templates.stream()
        .map(TemplateListDTO::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public PageResponseDto<TemplateListDTO> getTemplatePage(
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

    Page<TemplateListDTO> dtoPage = page.map(TemplateListDTO::from);

    return PageResponseDto.from(dtoPage);
  }

  /**
   * 정렬 기준 생성
   */
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

}
