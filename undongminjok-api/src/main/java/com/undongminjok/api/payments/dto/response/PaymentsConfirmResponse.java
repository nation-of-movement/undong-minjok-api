package com.undongminjok.api.payments.dto.response;


import com.undongminjok.api.payments.dto.PaymentInfoDTO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentsConfirmResponse {

  private PaymentInfoDTO paymentInfo;
}
