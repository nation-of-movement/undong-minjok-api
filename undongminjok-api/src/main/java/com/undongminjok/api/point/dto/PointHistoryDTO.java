package com.undongminjok.api.point.dto;

import com.undongminjok.api.point.domain.PointStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointHistoryDTO {

  private Long userId;
  private Long templateId;
  private PointStatus status;
  private Integer amount;
  private String method;
  private String bank;
  private String accountNumber;
  private LocalDateTime createdAt;
  private String orderId;





}
