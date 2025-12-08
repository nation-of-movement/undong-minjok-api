package com.undongminjok.api.templates.service.service.provider;


import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateProviderServiceImpl implements TemplateProviderService{

  private final TemplateRepository templateRepository;

  /**
   * 템플릿 조회
   * @param templateId
   * @return
   */
  @Override
  public Template getTemplate(Long templateId) {
    return templateRepository.findById(templateId)
        .orElse(null);
  }
}
