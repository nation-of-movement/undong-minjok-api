package com.undongminjok.api.templates.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TemplateUpdateRequestDTO {

  private String content;   // 설명 수정
  private Long price;       // 가격 수정
}
