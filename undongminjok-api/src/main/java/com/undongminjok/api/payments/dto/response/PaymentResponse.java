package com.undongminjok.api.payments.dto.response;

import com.undongminjok.api.user.domain.User;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PaymentResponse {

  private String version;
  private String paymentKey;
  private String orderId;
  private Long amount;
  private String currency;
  private String method;
  private String status;

  private OffsetDateTime requestedAt;
  private OffsetDateTime approvedAt;

  private String orderName;

  // 결제자 정보
  private User user;

}
