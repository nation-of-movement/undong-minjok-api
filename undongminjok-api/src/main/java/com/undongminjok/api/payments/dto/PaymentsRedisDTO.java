package com.undongminjok.api.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentsRedisDTO {

  private Long userId;
  private String orderId;
  private int amount;
  private String paymentKey;

}
