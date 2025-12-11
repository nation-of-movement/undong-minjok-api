package com.undongminjok.api.payments.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {

  private String orderId;
  private String paymentKey;
  private int amount;

}
