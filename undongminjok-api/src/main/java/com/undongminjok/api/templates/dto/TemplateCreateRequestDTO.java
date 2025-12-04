package com.undongminjok.api.templates.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateCreateRequestDTO {

  private String name;
  private String content;
  private String picture;
  private Long price;

}
