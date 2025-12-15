package com.undongminjok.api.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.payments.PaymentsErrorCode;
import com.undongminjok.api.payments.client.TossPaymentClient;
import com.undongminjok.api.payments.dto.PaymentsRedisDTO;
import com.undongminjok.api.payments.dto.TossConfirmRequest;
import com.undongminjok.api.payments.dto.response.PaymentResponse;
import com.undongminjok.api.payments.dto.response.TossConfirmResponse;
import com.undongminjok.api.payments.dto.request.PaymentsRequest;
import com.undongminjok.api.point.domain.PaymentMethod;
import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.PointHistoryDTO;
import com.undongminjok.api.point.service.provider.PointProviderService;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.service.provider.UserProviderService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsService {

  private final TossPaymentClient tossPaymentClient;
  private final PaymentsRedisService paymentsRedisService;
  private final PointProviderService  pointProviderService;
  private final UserProviderService userProviderService;
  private final ObjectMapper objectMapper;

  /**
   * redis에 orderId, amount 저장
   * @param request
   */
  public void prepare(PaymentsRequest request) {
    // userId 확인
    Long userId = Optional.ofNullable(SecurityUtil.getLoginUserInfo().getUserId())
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    // dto builder
    PaymentsRedisDTO dto = PaymentsRedisDTO.builder()
        .userId(userId)
        .orderId(request.getOrderId())
        .amount(request.getAmount())
        .paymentKey(request.getPaymentKey())
        .build();

    // redis 저장
    paymentsRedisService.savePayments(dto);

  }

  /**
   * 토스 결제 승인
   * @param request
   * @return
   */
  @Transactional
  public PaymentResponse confirmPayment(TossConfirmRequest request) {

    // userId 확인
    Long userId = Optional.ofNullable(SecurityUtil.getLoginUserInfo().getUserId())
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    // toss 서버에 결제 승인 요청
    TossConfirmResponse response = tossPaymentClient.confirmPayment(request);

    // 레스디스에 정보 확인
    PaymentsRedisDTO param = PaymentsRedisDTO.builder()
        .userId(userId)
        .orderId(response.getOrderId())
        .amount(Math.toIntExact(response.getTotalAmount()))
        .paymentKey(response.getPaymentKey())
        .build();

    Boolean exist = paymentsRedisService.findPayments(param);
    if(Boolean.FALSE.equals(exist)) {
      throw new BusinessException(PaymentsErrorCode.PAYMENTS_ERROR_CODE);
    }

    // 포인트 히스토리 추가
    PointHistoryDTO history = PointHistoryDTO.builder()
        .userId(userId)
        .templateId(null)
        .status(PointStatus.RECHARGE)
        .amount(Math.toIntExact(response.getTotalAmount()))
        .method(response.getMethod())
        .bank(null)
        .orderId(response.getOrderId())
        .accountNumber(null)
        .build();

    User user = userProviderService.getUser(userId);
    pointProviderService.createPointHistory(user, history);

    // 레디스 제거
    paymentsRedisService.deletePayments(param);
    return PaymentResponse.builder()
       .amount(Math.toIntExact(response.getTotalAmount()))
       .createdDt(response.getApprovedAt())
       .status(PointStatus.RECHARGE)
       .method(response.getMethod())
       .build();
  }


}
