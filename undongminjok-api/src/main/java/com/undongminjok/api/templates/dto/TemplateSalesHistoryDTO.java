package com.undongminjok.api.templates.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TemplateSalesHistoryDTO {

  private Long templateId;         // 템플릿 ID
  private String templateName;     // 제목
  private Long price;              // 가격
  private Long salesCount;         // 판매량
  private LocalDateTime createdAt; // 등록 날짜 (BaseTimeEntity.createdAt)
}
