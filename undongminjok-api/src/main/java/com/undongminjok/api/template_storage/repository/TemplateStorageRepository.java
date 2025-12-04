package com.undongminjok.api.template_storage.repository;

import com.undongminjok.api.template_storage.domain.TemplateStorage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateStorageRepository extends JpaRepository<TemplateStorage, Long> {

  boolean existsByUserUserIdAndTemplateId(Long userId, Long templateId);

  void deleteByUserUserIdAndTemplateId(Long userId, Long templateId);

  List<TemplateStorage> findAllByUserUserId(Long userId);

}
