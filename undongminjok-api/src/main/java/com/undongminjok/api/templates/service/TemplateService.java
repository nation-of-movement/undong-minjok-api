package com.undongminjok.api.templates.service;

import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.dto.TemplateDetailDTO;
import com.undongminjok.api.templates.dto.TemplateListDTO;
import com.undongminjok.api.templates.repository.TemplateRecommendRepository;
import com.undongminjok.api.templates.repository.TemplateRepository;

import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateService {

  private final TemplateRepository templateRepository;
  private final TemplateRecommendRepository recommendRepository;
  private final UserRepository userRepository;
  private final SecurityUtil securityUtil;

  // 1) 리스트 조회
  public List<TemplateListDTO> findByTemplateName(String keyword) {
    return templateRepository.findByNameContaining(keyword)
        .stream()
        .map(TemplateListDTO::from)
        .toList();
  }

  // 2) 상세 조회
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
        template.getWorkoutPlan()
            .getExercises()
            .stream()
            .map(WorkoutPlanExerciseDTO::from)
            .toList();

    return TemplateDetailDTO.builder()
        .id(template.getId())
        .name(template.getName())
        .content(template.getContent())
        .picture(template.getPicture())
        .price(template.getPrice())
        .salesCount(template.getSalesCount())
        .recommendCount(template.getRecommendCount())
        .recommended(recommended)
        .writerNickname(template.getUser().getNickname())
        .exercises(exercises)
        .createdAt(template.getCreatedAt().toString())
        .updatedAt(template.getUpdatedAt().toString())
        .build();
  }

  // 3) 템플릿 생성
  @Transactional
  public TemplateDetailDTO createTemplate(String picture, String name, String content, Long price) {

    Long userId = securityUtil.getLoginUserInfo().getUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

    Template template = Template.builder()
        .picture(picture)
        .name(name)
        .content(content)
        .price(price)
        .user(user)
        .build();

    templateRepository.save(template);

    return TemplateDetailDTO.builder()
        .id(template.getId())
        .name(template.getName())
        .picture(template.getPicture())
        .price(template.getPrice())
        .content(template.getContent())
        .build();
  }

  // 4) 수정
  @Transactional
  public TemplateDetailDTO updateTemplate(Long id, String picture, String content, Long price) {

    Template template = templateRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("템플릿 없음"));

    template.update(picture, content, price);

    return TemplateDetailDTO.builder()
        .id(template.getId())
        .name(template.getName())
        .picture(template.getPicture())
        .price(template.getPrice())
        .content(template.getContent())
        .build();
  }

  // 5) 삭제
  @Transactional
  public void deleteTemplate(Long id) {

    if (!templateRepository.existsById(id)) {
      throw new IllegalArgumentException("삭제할 템플릿 없음");
    }

    templateRepository.deleteById(id);
  }

}


