package com.undongminjok.api.templates.service;

import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.domain.TemplateRecommend;
import com.undongminjok.api.templates.repository.TemplateRecommendRepository;
import com.undongminjok.api.templates.repository.TemplateRepository;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateRecommendService {

  private final TemplateRecommendRepository recommendRepository;
  private final TemplateRepository templateRepository;
  private final UserRepository userRepository;

  // 추천 등록
  @Transactional
  public void recommend(Long templateId) {
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

    Template template = templateRepository.findById(templateId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 템플릿"));

    // 중복 추천 체크
    recommendRepository.findByUserAndTemplate(user, template)
        .ifPresent(r -> {
          throw new IllegalStateException("이미 추천한 템플릿입니다.");
        });

    // 추천 저장
    recommendRepository.save(new TemplateRecommend(user, template));

    // 추천수 +1
    template.increaseRecommend();
  }

  // 추천 취소
  @Transactional
  public void cancel(Long templateId) {

    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

    Template template = templateRepository.findById(templateId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 템플릿"));

    TemplateRecommend recommend = recommendRepository.findByUserAndTemplate(user, template)
        .orElseThrow(() -> new IllegalStateException("추천하지 않은 템플릿입니다."));

    // 추천 삭제
    recommendRepository.delete(recommend);

    // 추천수 -1
    template.decreaseRecommend();
  }

  // 현재 유저가 추천했는지 체크
  public boolean isRecommended(Long templateId, Long userId) {
    User user = userRepository.findById(userId).orElseThrow();
    Template template = templateRepository.findById(templateId).orElseThrow();

    return recommendRepository.findByUserAndTemplate(user, template).isPresent();
  }

  //  유저가 추천한 템플릿 목록 조회
  // ================================
  public List<Template> getUserRecommendedTemplates(Long userId) {

    User user = userRepository.findById(userId)
        .orElseThrow();

    List<TemplateRecommend> recommends =
        recommendRepository.findAllByUser(user);

    return recommends.stream()
        .map(TemplateRecommend::getTemplate)
        .toList();
  }


}
