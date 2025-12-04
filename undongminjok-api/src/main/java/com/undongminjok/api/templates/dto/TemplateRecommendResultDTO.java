package com.undongminjok.api.templates.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemplateRecommendResultDTO {
  private Long templateId;
  private boolean recommended;  // true → 추천됨, false → 취소됨
  private Long recommendCount;

}
