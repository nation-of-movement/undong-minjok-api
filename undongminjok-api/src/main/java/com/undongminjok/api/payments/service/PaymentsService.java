package com.undongminjok.api.payments.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.payments.PaymentsErrorCode;
import com.undongminjok.api.payments.dto.PaymentInfoDTO;
import com.undongminjok.api.payments.dto.PaymentsRedisDTO;
import com.undongminjok.api.payments.dto.request.PaymentsRequest;
import com.undongminjok.api.payments.dto.response.PaymentResponse;
import com.undongminjok.api.payments.dto.response.PaymentsConfirmResponse;
import com.undongminjok.api.point.domain.PointStatus;
import com.undongminjok.api.point.dto.PointHistoryDTO;
import com.undongminjok.api.point.service.provider.PointProviderService;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.service.provider.UserProviderService;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentsService {

  private final PaymentsRedisService paymentsRedisService;
  private final PointProviderService  pointProviderService;
  private final UserProviderService userProviderService;
  private final ObjectMapper objectMapper;

  @Value("${tosspayments.secret-key}")
  private String secretKey;


  /**
   * redis에 orderId, amount 저장
   * @param request
   */
  public void prepare(PaymentsRequest request) {
    log.info("[ PaymentsService / prepare] :  redis에 orderId, amount 저장");
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
   * 결제 승인
   * @param
   */
  @Transactional
  public PaymentsConfirmResponse confirm(PaymentsRequest request) throws IOException, InterruptedException {
    Long userId = Optional.ofNullable(SecurityUtil.getLoginUserInfo().getUserId())
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    PaymentsRedisDTO param = PaymentsRedisDTO.builder()
        .userId(userId)
        .orderId(request.getOrderId())
        .amount(request.getAmount())
        .paymentKey(request.getPaymentKey())
        .build();

    // Redis 조회
    Boolean exist = paymentsRedisService.findPayments(param);
    if(Boolean.FALSE.equals(exist)) {
      throw new BusinessException(PaymentsErrorCode.PAYMENTS_ERROR_CODE);
    }

    // 토스 결제 승인
    HttpResponse approvedPayment = requestConfirm(request);
    log.info("결제 승인 완료: approvedPayment={}", approvedPayment);

    // 포인트 히스토리 등록
    PointHistoryDTO history = PointHistoryDTO.builder()
        .userId(userId)
        .templateId(null)
        .status(PointStatus.RECHARGE)
        .amount(request.getAmount())
        .method(null)
        .bank(null)
        .accountNumber(null)
        .build();

    // 히스토리 추가, user amount 수정
    User user = userProviderService.getUser(userId);
    pointProviderService.createPointHistory(user, history);

    // Redis 제거
    // paymentsRedisService.deletePayments(param);

    return PaymentsConfirmResponse.builder()
        .paymentInfo(null)
        .build();
  }


  public HttpResponse  requestConfirm(PaymentsRequest param) throws IOException, InterruptedException {
    String basicAuth = Base64.getEncoder()
    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    log.info("결제 승인 : basicAuth= {}", basicAuth);

    JsonNode requestObj = objectMapper.createObjectNode()
        .put("orderId", param.getOrderId())
        .put("amount", param.getAmount())
        .put("paymentKey", param.getPaymentKey());

    String requestBody = objectMapper.writeValueAsString(requestObj);
    log.info("결제 승인 : requestBody= {}", requestBody);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
        .header("Authorization", "Basic " + basicAuth)
        .header("Content-Type", "application/json")
        .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
        .build();
     return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

  }


}
