package com.undongminjok.api.templates.service;

import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.dto.TemplateResponseDTO;
import com.undongminjok.api.templates.repository.TemplateRepository;

import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TemplateService {

  private final TemplateRepository templateRepository;
  private final UserRepository userRepository;
  private final SecurityUtil securityUtil;


  //  조회 기능 (readOnly)
  public List<TemplateResponseDTO> findByTemplateName(String keyword) {

    return templateRepository.findByNameContaining(keyword)
        .stream()
        .map(TemplateResponseDTO::from)
        .toList();
  }

  //  템플릿 생성 (create)
  @Transactional
  public TemplateResponseDTO createTemplate(String picture,
      String name,
      String content,
      Long price) {


    Long userId = securityUtil.getLoginUserInfo().getUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

    //  createdAt, updatedAt은 Auditing이 자동으로 넣어줌
    Template template = Template.builder()
        .picture(picture)
        .name(name)
        .content(content)
        .price(price)
        .user(user)
        .build();

    Template saved = templateRepository.save(template);

    return TemplateResponseDTO.from(saved);
  }

  //  템플릿 수정 (update)
  @Transactional
  public TemplateResponseDTO updateTemplate(Long id,
      String picture,
      String content,
      Long price) {

    Template template = templateRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("템플릿을 찾을 수 없습니다."));

    // ⭐ updatedAt은 Auditing이 자동으로 업데이트
    template.update(picture, content, price);

    return TemplateResponseDTO.from(template);
  }

  //  템플릿 삭제 (delete)
  @Transactional
  public void deleteTemplate(Long id) {

    if (!templateRepository.existsById(id)) {
      throw new IllegalArgumentException("삭제할 템플릿이 존재하지 않습니다.");
    }

    templateRepository.deleteById(id);
  }

}


