package com.undongminjok.api.templates.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TemplatePurchaseHistoryDTO {

  private Long templateId;
  private String templateName;
  private Long price;
  private LocalDateTime purchasedAt;
}
