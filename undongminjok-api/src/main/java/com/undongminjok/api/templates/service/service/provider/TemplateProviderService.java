package com.undongminjok.api.templates.service.service.provider;

import com.undongminjok.api.templates.domain.Template;

public interface TemplateProviderService {

  // template 조회
  Template getTemplate(Long templateId);

}
