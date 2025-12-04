package com.undongminjok.api.template_storage.service;

import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.template_storage.domain.TemplateStorage;
import com.undongminjok.api.template_storage.repository.TemplateStorageRepository;
import com.undongminjok.api.templates.TemplateErrorCode;
import com.undongminjok.api.templates.domain.Template;
import com.undongminjok.api.templates.repository.TemplateRepository;
import com.undongminjok.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateStorageCommandService {

  private final TemplateStorageRepository storageRepo;
  private final TemplateRepository templateRepo;
  private final UserRepository userRepo;

  //템플릿 저장
  public void saveTemplateToStorage(Long templateId) {
    //로그인한 유저 가져오기
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    //템플릿 있는건지 검증
    Template template = templateRepo.findById(templateId)
        .orElseThrow(() -> new BusinessException(TemplateErrorCode.TEMPLATE_NOT_FOUND));

    //이미 저장된 템플릿이면 작업 없이 종료
    if (storageRepo.existsByUserUserIdAndTemplateId(userId, templateId)) return;

    //템플릿 엔티티 생성 (user + template)
    TemplateStorage storage = TemplateStorage.builder()
        .user(userRepo.getReferenceById(userId))
        .template(template)
        .build();

    storageRepo.save(storage);
  }

  public void deleteTemplateFromStorage(Long templateId) {
    Long userId  = SecurityUtil.getLoginUserInfo().getUserId();
    storageRepo.deleteByUserUserIdAndTemplateId(userId, templateId);
  }
}
