package com.undongminjok.api.templates.repository;

import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.domain.TemplateRecommend;
import com.undongminjok.api.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRecommendRepository extends JpaRepository<TemplateRecommend, Long> {

  Optional<TemplateRecommend> findByUserAndTemplate(User user, Template template);

  Long countByTemplate(Template template);

  // ⭐ 유저가 추천한 템플릿 전체 목록 조회용
  List<TemplateRecommend> findAllByUser(User user);

    void deleteAllByTemplateId(Long templateId);
}

