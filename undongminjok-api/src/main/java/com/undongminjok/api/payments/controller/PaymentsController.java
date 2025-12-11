package com.undongminjok.api.payments.controller;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.payments.dto.request.PaymentsRequest;
import com.undongminjok.api.payments.dto.response.PaymentsConfirmResponse;
import com.undongminjok.api.payments.service.PaymentsService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  /**
   * 결제 승인
   * @param request
   * @return
   */
  @PostMapping("/confirm")
  ResponseEntity<ApiResponse<PaymentsConfirmResponse>> chargePoint(
      @RequestBody PaymentsRequest request) throws IOException, InterruptedException {

    PaymentsConfirmResponse response = paymentsService.confirm(request);

    return ResponseEntity.ok(ApiResponse.success(response));
  }


}
