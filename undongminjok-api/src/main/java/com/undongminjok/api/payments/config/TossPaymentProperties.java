package com.undongminjok.api.payments.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "toss.payments")
public class TossPaymentProperties {

  private final String secretKey;
  private final String clientKey;
  private final String baseUrl;
  private final String successUrl;
  private final String failUrl;

  public TossPaymentProperties(String secretKey,
      String clientKey,
      String baseUrl,
      String successUrl,
      String failUrl) {
    this.secretKey = secretKey;
    this.clientKey = clientKey;
    this.baseUrl = baseUrl;
    this.successUrl = successUrl;
    this.failUrl = failUrl;
  }

}
