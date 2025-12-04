package com.undongminjok.api.template_storage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TemplateStorageListResponse {

  //템플릿 보관함 목록조회
  private Long templateId;
  private String templateName;
  private String templateContent;
  private String imgPath;
  private String creatorNickname;

}
