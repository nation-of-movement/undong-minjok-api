package com.undongminjok.api.template_storage.repository;

import com.undongminjok.api.template_storage.domain.TemplateStorage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateStorageRepository extends JpaRepository<TemplateStorage, Long> {

  boolean existsByUserUserIdAndTemplateId(Long userId, Long templateId);

  void deleteByUserUserIdAndTemplateId(Long userId, Long templateId);

}
