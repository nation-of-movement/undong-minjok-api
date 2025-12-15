package com.undongminjok.api.payments.client;

import com.undongminjok.api.payments.config.TossPaymentProperties;
import com.undongminjok.api.payments.dto.TossConfirmRequest;
import com.undongminjok.api.payments.dto.response.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

  private final TossPaymentProperties properties;
  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * 토스 페이 결제 승인
   * @param request
   * @return
   */
  public TossConfirmResponse confirmPayment(TossConfirmRequest request) {

    String url = properties.getBaseUrl() + "/v1/payments/confirm";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    // secret-key: (Basic Auth 형식)
    headers.setBasicAuth(properties.getSecretKey(), "");

    HttpEntity<TossConfirmRequest> entity = new HttpEntity<>(request, headers);

    ResponseEntity<TossConfirmResponse> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, TossConfirmResponse.class);

    return response.getBody();
  }

}
