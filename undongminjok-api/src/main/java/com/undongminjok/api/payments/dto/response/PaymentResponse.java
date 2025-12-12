package com.undongminjok.api.payments.dto.response;

import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.user.domain.User;
import java.time.LocalDateTime;
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

   private PointStatus status; // 상태
   private String method; // 결제 method
   private Integer amount; // 포인트 결제 금액
   private String createdDt; // 결제시간

}
