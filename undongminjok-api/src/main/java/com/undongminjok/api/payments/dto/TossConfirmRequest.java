package com.undongminjok.api.payments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossConfirmRequest {

  private String paymentKey;  // Toss에서 넘겨줌
  private String orderId;     // 우리가 생성한 주문 ID
  private Long amount;        // 결제 금액
}
