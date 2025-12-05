package com.undongminjok.api.templates.service;

import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.storage.ImageCategory;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.dto.TemplateCreateRequestDTO;
import com.undongminjok.api.templates.dto.TemplateDetailDTO;
import com.undongminjok.api.templates.dto.TemplateListDTO;
import com.undongminjok.api.templates.dto.TemplateUpdateRequestDTO;
import com.undongminjok.api.templates.repository.TemplateRecommendRepository;
import com.undongminjok.api.templates.repository.TemplateRepository;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.repository.UserRepository;
import com.undongminjok.api.workoutplan.WorkoutPlanExerciseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateService {

  private final TemplateRepository templateRepository;
  private final TemplateRecommendRepository recommendRepository;
  private final UserRepository userRepository;
  private final SecurityUtil securityUtil;
  private final FileStorage fileStorage;

  // 1) 템플릿 리스트 조회
  public List<TemplateListDTO> findByTemplateName(String keyword) {
    return templateRepository.findByNameContaining(keyword)
        .stream()
        .map(TemplateListDTO::from)
        .toList();
  }

  // 2) 템플릿 상세 조회
  public TemplateDetailDTO getTemplateDetail(Long templateId) {

    Long loginUserId = securityUtil.getLoginUserInfo().getUserId();

    User loginUser = userRepository.findById(loginUserId)
        .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    Template template = templateRepository.findDetailById(templateId)
        .orElseThrow(() -> new IllegalArgumentException("템플릿 없음"));

    boolean recommended = recommendRepository
        .findByUserAndTemplate(loginUser, template)
        .isPresent();

    List<WorkoutPlanExerciseDTO> exercises =
        template.getWorkoutPlan().getExercises()
            .stream()
            .map(WorkoutPlanExerciseDTO::from)
            .toList();

    return TemplateDetailDTO.from(template, recommended, exercises);
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

    Template template = Template.builder()
        .name(req.getName())
        .content(req.getContent())
        .price(req.getPrice())
        .user(user)
        .build();

    // 템플릿 먼저 저장 (ID 필요할 수도 있음)
    templateRepository.save(template);

    // ⭐ 썸네일 이미지 업로드
    if (thumbnail != null && !thumbnail.isEmpty()) {
      String thumbPath = fileStorage.store(thumbnail, ImageCategory.THUMBNAIL);
      template.updateThumbnail(thumbPath);
    }

    // ⭐ 상세 이미지 업로드
    if (detailImage != null && !detailImage.isEmpty()) {
      String detailPath = fileStorage.store(detailImage, ImageCategory.DETAIL);
      template.updateTemplateImage(detailPath);
    }
  }


  // 4) 템플릿 수정 (텍스트 + 썸네일 + 상세이미지)
  @Transactional
  public void updateTemplate(
      Long id,
      TemplateUpdateRequestDTO req,
      MultipartFile thumbnail,
      MultipartFile detailImage
  ) {

    Template template = templateRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("템플릿 없음"));

    // 텍스트 수정
    template.update(
        req.getPicture(),
        req.getContent(),
        req.getPrice()
    );

    // 썸네일 수정
    if (thumbnail != null && !thumbnail.isEmpty()) {
      if (template.getThumbnailImage() != null) {
        fileStorage.deleteQuietly(template.getThumbnailImage());
      }
      String newThumbPath = fileStorage.store(thumbnail, ImageCategory.THUMBNAIL);
      template.updateThumbnail(newThumbPath);
    }

    // 상세이미지 수정
    if (detailImage != null && !detailImage.isEmpty()) {
      if (template.getTemplateImage() != null) {
        fileStorage.deleteQuietly(template.getTemplateImage());
      }
      String newDetailPath = fileStorage.store(detailImage, ImageCategory.DETAIL);
      template.updateTemplateImage(newDetailPath);
    }
  }

  // 5) 템플릿 삭제
  @Transactional
  public void deleteTemplate(Long id) {

    Template template = templateRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("삭제할 템플릿 없음"));

    if (template.getThumbnailImage() != null)
      fileStorage.deleteQuietly(template.getThumbnailImage());

    if (template.getTemplateImage() != null)
      fileStorage.deleteQuietly(template.getTemplateImage());

    templateRepository.delete(template);
  }
}