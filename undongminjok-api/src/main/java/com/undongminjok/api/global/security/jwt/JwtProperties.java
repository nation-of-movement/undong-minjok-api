package com.undongminjok.api.global.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/*
* JWT 내부 정보 관련 DTO
* */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private String secretKey;
  private long expiration;
  private long refreshExpiration;
}
