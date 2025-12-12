package com.undongminjok.api.payments.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentsRequest {

  private String orderId;
  private Integer amount;
  private String paymentKey;

}
