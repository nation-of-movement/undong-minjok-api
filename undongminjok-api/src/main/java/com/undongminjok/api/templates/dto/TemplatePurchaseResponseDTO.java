package com.undongminjok.api.templates.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TemplatePurchaseResponseDTO {

  private Long templateId;
  private String templateName;
  private Long price;
}

