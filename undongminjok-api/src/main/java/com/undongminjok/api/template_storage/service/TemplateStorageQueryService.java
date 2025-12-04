package com.undongminjok.api.template_storage.service;

import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.template_storage.dto.response.TemplateStorageListResponse;
import com.undongminjok.api.template_storage.repository.TemplateStorageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateStorageQueryService {

  //목록조회
  private final TemplateStorageRepository storageRepo;

  public List<TemplateStorageListResponse> getMyTemplateStorageList(){

    //로그인한 유저 가져오기
    Long userId = SecurityUtil.getLoginUserInfo().getUserId();

    var storages = storageRepo.findAllByUserUserId(userId);

    return storages.stream()
        .map(storage -> {
          var tpl = storage.getTemplate();

          return TemplateStorageListResponse.builder()
              .templateId(tpl.getId())
              .templateName(tpl.getName())
              .templateContent(tpl.getContent())
              .imgPath(tpl.getPicture())
              .creatorName(
                  tpl.getUser() != null
                      ? tpl.getUser().getNickname()  // User 엔티티 필드명 맞게 수정
                      : null
              )
              .build();
        })
        .toList();
  }
}
