package com.undongminjok.api.payments.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardInfo {
  private Integer amount;
  private String issuerCode;
  private String acquirerCode;
  private String number;
  private Integer installmentPlanMonths;
  private String approveNo;
  private String cardType;
  private String ownerType;
  private String acquireStatus;
  private Boolean isInterestFree;
}
