package com.undongminjok.api.payments.controller;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.payments.dto.TossConfirmRequest;
import com.undongminjok.api.payments.dto.response.PaymentResponse;
import com.undongminjok.api.payments.dto.response.TossConfirmResponse;
import com.undongminjok.api.payments.dto.request.PaymentsRequest;
import com.undongminjok.api.payments.service.PaymentsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Payments",
    description = "포인트 충전을 위한 결제 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentsController {

  private final PaymentsService paymentsService;

  /**
   * 결제 요청시 orderId, amount redis 저장
   * @param request
   * @return
   */
  @PostMapping("/prepare")
  ResponseEntity<ApiResponse<Void>> prepare(
      @RequestBody PaymentsRequest request) {

    paymentsService.prepare(request);

    return ResponseEntity.ok(ApiResponse.success(null));
  }

/*  *//**
   * 결제 승인
   * @param request
   * @return
   */
  @PostMapping("/confirm")
  ResponseEntity<ApiResponse<PaymentResponse>> chargePoint(
      @RequestBody TossConfirmRequest request) {

    PaymentResponse response = paymentsService.confirmPayment(request);

    return ResponseEntity.ok(ApiResponse.success(response));
  }





}


