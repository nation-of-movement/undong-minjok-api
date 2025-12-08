package com.undongminjok.api.template_storage.repository;

import com.undongminjok.api.template_storage.domain.TemplateStorage;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateStorageRepository extends JpaRepository<TemplateStorage, Long> {

  boolean existsByUserUserIdAndTemplateId(Long userId, Long templateId);

  void deleteByUserUserIdAndTemplateId(Long userId, Long templateId);

  List<TemplateStorage> findAllByUserUserId(Long userId);

// 템플릿 구매시 이미 보관함에 존재하는지 확인
  boolean existsByUserAndTemplate(User userId, Template templateId);
}
