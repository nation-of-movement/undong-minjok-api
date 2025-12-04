package com.undongminjok.api.templates.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TemplateUpdateRequestDTO {

  private String content;   // ✔ 수정 가능
  private String picture;   // ✔ 수정 가능
  private Long price;       // ✔ 수정 가능

}