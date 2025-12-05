package com.undongminjok.api.template_storage.service;

import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.template_storage.domain.TemplateStorage;
import com.undongminjok.api.template_storage.dto.response.TemplateStorageListResponse;
import com.undongminjok.api.template_storage.repository.TemplateStorageRepository;
import com.undongminjok.api.templates.TemplateErrorCode;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.repository.TemplateRepository;
import com.undongminjok.api.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TemplateStorageService {

  private final TemplateStorageRepository storageRepository;
  private final TemplateRepository templateRepository;
  private final UserRepository userRepository;

  //템플릿 저장
  @Transactional
  public void saveTemplateToStorage(Long templateId) {
    //로그인한 유저 가져오기
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    //템플릿 있는건지 검증
    Template template = templateRepository.findById(templateId)
        .orElseThrow(() -> new BusinessException(TemplateErrorCode.TEMPLATE_NOT_FOUND));

    //이미 저장된 템플릿이면 작업 없이 종료
    if (storageRepository.existsByUserUserIdAndTemplateId(userId, templateId)) return;

    //템플릿 엔티티 생성 (user + template)
    TemplateStorage storage = TemplateStorage.builder()
        .user(userRepository.getReferenceById(userId))
        .template(template)
        .build();

    storageRepository.save(storage);
  }

  //삭제
  @Transactional
  public void deleteTemplateFromStorage(Long templateId) {
    Long userId  = SecurityUtil.getLoginUserInfo().getUserId();
    storageRepository.deleteByUserUserIdAndTemplateId(userId, templateId);
  }

  //조회
  @Transactional(readOnly = true)
  public List<TemplateStorageListResponse> getMyTemplateStorageList(){

    //로그인한 유저 가져오기
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    var storages = storageRepository.findAllByUserUserId(userId);

    return storages.stream()
        .map(storage -> {
          var tpl = storage.getTemplate();

          return TemplateStorageListResponse.builder()
              .templateId(tpl.getId())
              .templateName(tpl.getName())
              .templateContent(tpl.getContent())
              .imgPath(tpl.getThumbnailImage())
              .creatorNickname(
                  tpl.getUser() != null
                      ? tpl.getUser().getNickname()  // User 엔티티 필드명 맞게 수정
                      : null
              )
              .build();
        })
        .toList();
  }
}
