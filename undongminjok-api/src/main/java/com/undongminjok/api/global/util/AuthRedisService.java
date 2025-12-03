package com.undongminjok.api.global.util;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthRedisService {

  private final long EMAIL_VALIDITY_SECONDS = 5; // 인증번호 기본 시간
  private final long REFRESH_TOKEN_EXPIRY = 14; // 2주

  private final RedisTemplate<String, Object> redisTemplate;

  public void saveRefreshToken(String loginId, String refreshToken) {
    String key = "refreshToken:" + loginId;

    redisTemplate.opsForValue().set(
        key, refreshToken, REFRESH_TOKEN_EXPIRY, TimeUnit.DAYS);
  }

}
