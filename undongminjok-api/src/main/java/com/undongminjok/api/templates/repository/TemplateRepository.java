package com.undongminjok.api.templates.repository;

import com.undongminjok.api.templates.domain.Template;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Long> {

  // 템플릿 이름에 keyword가 포함된 모든 템플릿 검색
  List<Template> findByNameContaining(String keyword);

}