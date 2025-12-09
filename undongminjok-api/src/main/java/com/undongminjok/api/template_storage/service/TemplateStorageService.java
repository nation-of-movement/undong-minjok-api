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
        .deleted(false)
        .build();

    storageRepository.save(storage);
  }

  //삭제
  @Transactional
  public void deleteTemplateFromStorage(Long templateId) {
    Long userId  = SecurityUtil.getLoginUserInfo().getUserId();

    //저장된 엔티티 가져오기 (soft delete적용)
    List<TemplateStorage> storages = storageRepository.findAllByTemplateId(templateId)
            .stream()
            .filter(s -> s.getUser().getUserId().equals(userId))
            .toList();

    if (storages.isEmpty()) return;

    TemplateStorage storage = storages.get(0);

    //soft delete
    storage.markAsDeleted();

    Template template = storage.getTemplate();
    //내가만든 템플릿인지 확인
    boolean isOwner = template.getUser() != null &&
            template.getUser().getUserId().equals(userId);

    if (!isOwner) return;

    //내가 만든 템플릿인지 판단
    if (template.getSalesCount() > 0) {
        // 판매된 적이 있으므로 hard delete 불가 → 예외 발생
        throw new BusinessException(TemplateErrorCode.TEMPLATE_CANNOT_DELETE_SOLD);
    }
    //판매된적 없으면 hard delete
    templateRepository.delete(template);
  }

  //조회
  @Transactional(readOnly = true)
  public List<TemplateStorageListResponse> getMyTemplateStorageList() {
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

     /*
      * 보관함에서 삭제되지 않은 템플릿 가져오기
      *    → 내가 다운받아서 저장한 템플릿들
      */
      var storageTemplates = storageRepository.findAllByUserUserIdAndDeletedFalse(userId)
              .stream()
              .map(storage -> storage.getTemplate())
              .toList();

      /*
       * templates 테이블에서 내가 만든 템플릿 가져오기
       *    → user_id = 나인 템플릿들
       */
      var myOwnTemplates = templateRepository.findAllByUserUserId(userId);

      /*
       * 두 리스트 합치기
       *    보관함 저장 템플릿 + 내가 만든 템플릿
       */
      var allTemplates = new java.util.ArrayList<Template>();
      allTemplates.addAll(storageTemplates);
      allTemplates.addAll(myOwnTemplates);
      /*
       * ✔ 4. Template → Response DTO 로 변환
       */
      return allTemplates.stream()
              .map(tpl -> TemplateStorageListResponse.builder()
                      .templateId(tpl.getId())
                      .templateName(tpl.getName())
                      .templateContent(tpl.getContent())
                      .imgPath(tpl.getThumbnailImage())
                      .creatorNickname(
                              tpl.getUser() != null
                                      ? tpl.getUser().getNickname()
                                      : null
                      )
                      .build()
              )
              .toList();
  }
}
