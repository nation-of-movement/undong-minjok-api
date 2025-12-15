package com.undongminjok.api.payments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PaymentInfoDTO {

  private String version;
  private String paymentKey;
  private String type;
  private String orderId;
  private String orderName;
  private String mId;
  private String currency;
  private String method;
  private Integer totalAmount;
  private Integer balanceAmount;
  private String status;
  private String requestedAt;
  private String approvedAt;
  private Boolean useEscrow;
  private CardInfo card;
  private FailureInfo failure;

}
