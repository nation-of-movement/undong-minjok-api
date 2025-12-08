package com.undongminjok.api.templates.service;

import com.undongminjok.api.equipments.repository.EquipmentRepository;
import com.undongminjok.api.equipments.domain.Equipment;
import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.domain.Template;
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

  // 1) 템플릿 리스트 조회
  public List<TemplateListDTO> findByTemplateName(String keyword) {
    return templateRepository.findByNameContaining(keyword)
        .stream()
        .map(TemplateListDTO::from)
        .toList();
  }

  // 2) 템플릿 상세조회 (day 그룹핑)
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


  // 3) 템플릿 생성
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

  // 4) 템플릿 수정
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

  // 5) 템플릿 삭제 (Hard + Soft Delete 적용)
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
}
