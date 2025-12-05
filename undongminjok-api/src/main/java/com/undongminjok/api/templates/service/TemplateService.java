package com.undongminjok.api.templates.service;

import com.undongminjok.api.global.storage.FileStorage;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.dto.TemplateCreateRequestDTO;
import com.undongminjok.api.templates.dto.TemplateDetailDTO;
import com.undongminjok.api.templates.dto.TemplateListDTO;
import com.undongminjok.api.templates.dto.TemplateUpdateRequestDTO;
import com.undongminjok.api.templates.image.TemplateImageCategory;
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
  private final FileStorage fileStorage; // ⭐ 글로벌 파일 스토리지 사용

  // ---------------------------
  // 1) 템플릿 리스트 조회
  // ---------------------------
  public List<TemplateListDTO> findByTemplateName(String keyword) {
    return templateRepository.findByNameContaining(keyword)
        .stream()
        .map(TemplateListDTO::from)
        .toList();
  }

  // ---------------------------
  // 2) 상세 조회
  // ---------------------------
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

  // ---------------------------
  // 3) 템플릿 생성
  // ---------------------------
  @Transactional
  public TemplateDetailDTO createTemplate(TemplateCreateRequestDTO req) {

    Long userId = securityUtil.getLoginUserInfo().getUserId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

    Template template = Template.builder()
        .picture(req.getPicture())  // 기본 대표 이미지
        .name(req.getName())
        .content(req.getContent())
        .price(req.getPrice())
        .user(user)
        .build();

    templateRepository.save(template);

    return TemplateDetailDTO.from(template);
  }

  // ---------------------------
  // 4) 템플릿 수정
  // ---------------------------
  @Transactional
  public TemplateDetailDTO updateTemplate(Long id, TemplateUpdateRequestDTO req) {

    Template template = templateRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("템플릿 없음"));

    template.update(req.getPicture(), req.getContent(), req.getPrice());

    return TemplateDetailDTO.from(template);
  }

  // ---------------------------
  // 5) 템플릿 썸네일 업로드
  // ---------------------------
  @Transactional
  public void updateThumbnailImage(Long templateId, MultipartFile file) {

    Template template = templateRepository.findById(templateId)
        .orElseThrow(() -> new IllegalArgumentException("템플릿 없음"));

    // 기존 파일 삭제
    if (template.getThumbnailImage() != null) {
      fileStorage.deleteQuietly(template.getThumbnailImage());
    }

    // 업로드
    String path = fileStorage.store(file, TemplateImageCategory.THUMBNAIL);

    template.updateThumbnail(path);
  }

  // ---------------------------
  // 6) 템플릿 상세 이미지 업로드
  // ---------------------------
  @Transactional
  public void updateTemplateImage(Long templateId, MultipartFile file) {

    Template template = templateRepository.findById(templateId)
        .orElseThrow(() -> new IllegalArgumentException("템플릿 없음"));

    // 기존 파일 삭제
    if (template.getTemplateImage() != null) {
      fileStorage.deleteQuietly(template.getTemplateImage());
    }

    // 업로드
    String path = fileStorage.store(file, TemplateImageCategory.DETAIL);

    template.updateTemplateImage(path);
  }

  // ---------------------------
  // 7) 템플릿 삭제
  // ---------------------------
  @Transactional
  public void deleteTemplate(Long id) {

    Template template = templateRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("삭제할 템플릿 없음"));

    // 저장되어 있던 파일 삭제
    if (template.getThumbnailImage() != null)
      fileStorage.deleteQuietly(template.getThumbnailImage());

    if (template.getTemplateImage() != null)
      fileStorage.deleteQuietly(template.getTemplateImage());

    templateRepository.delete(template);
  }
}