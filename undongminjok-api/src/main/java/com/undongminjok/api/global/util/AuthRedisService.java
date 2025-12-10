package com.undongminjok.api.global.util;

import com.undongminjok.api.auth.AuthErrorCode;
import com.undongminjok.api.auth.dto.EmailRequest;
import com.undongminjok.api.auth.dto.VerificationCodeRequest;
import com.undongminjok.api.global.exception.BusinessException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthRedisService {

  private final long EMAIL_VALIDITY_SECONDS = 5; // 인증번호 기본 시간
  private final long REFRESH_TOKEN_EXPIRY = 14; // 2주
  private final long RESET_TOKEN_VALIDITY_MINUTES = 15; // 비밀번호 재설정 토큰 유효시간

  private final RedisTemplate<String, Object> redisTemplate;

  /* ========== Refresh Token ========== */

  public void saveRefreshToken(String loginId, String refreshToken) {
    String key = "refreshToken:" + loginId;

    redisTemplate.opsForValue()
                 .set(
                     key, refreshToken, REFRESH_TOKEN_EXPIRY, TimeUnit.DAYS);
  }

  public Boolean existRefreshTokenByLoginId(String loginId) {
    String key = "refreshToken:" + loginId;

    return redisTemplate.hasKey(key);
  }

  public String findKeyByLoginId(String loginId) {
    String key = "refreshToken:" + loginId;

    return redisTemplate.opsForValue()
                        .get(key)
                        .toString();
  }

  public void deleteRefreshTokenByLoginId(String loginId) {
    String key = "refreshToken:" + loginId;

    if (redisTemplate.hasKey(key)) {
      redisTemplate.delete(key);
    }
  }

  /* ========== BlackList Access Token ========== */

  public void addBlackListAccessToken(String accessToken, long remainingTime) {

    String key = "blackList:" + accessToken;

    redisTemplate.opsForValue()
                 .set(key, "logout", remainingTime, TimeUnit.MILLISECONDS);
  }

  public boolean isBlacklisted(String accessToken) {
    return redisTemplate.hasKey("blackList:" + accessToken);
  }

  /* ========== Email Verification Code ========== */

  public void saveVerificationCode(EmailRequest request, String code) {
    String key = "email:" + request.getPurpose() + ":" + request.getEmail();

    redisTemplate.opsForValue()
                 .set(key, code, EMAIL_VALIDITY_SECONDS, TimeUnit.MINUTES);

  }

  public Boolean existVerificationCode(VerificationCodeRequest request) {
    String key = "email:" + request.getPurpose() + ":" + request.getEmail();

    if (!redisTemplate.hasKey(key)) {
      throw new BusinessException(AuthErrorCode.INVALID_VERIFICATION_CODE);
    }

    String originCode = redisTemplate.opsForValue()
                                     .get(key)
                                     .toString();

    return Objects.equals(originCode, request.getCode());
  }

  public void deleteKeyEmail(VerificationCodeRequest request) {

    String key = "email:" + request.getPurpose() + ":" + request.getEmail();

    if (redisTemplate.hasKey(key)) {
      redisTemplate.delete(key);
    }
  }

  /* ========== Password Reset Token ========== */

  public String createAndSaveResetToken(String email) {
    String resetToken = UUID.randomUUID().toString();
    String key = "resetToken:" + resetToken;

    redisTemplate.opsForValue()
                 .set(key, email, RESET_TOKEN_VALIDITY_MINUTES, TimeUnit.MINUTES);

    return resetToken;
  }

  public String getEmailByResetToken(String resetToken) {
    String key = "resetToken:" + resetToken;

    if (!redisTemplate.hasKey(key)) {
      throw new BusinessException(AuthErrorCode.INVALID_RESET_TOKEN);
    }

    Object value = redisTemplate.opsForValue().get(key);
    if (value == null) {
      throw new BusinessException(AuthErrorCode.INVALID_RESET_TOKEN);
    }

    return value.toString();
  }

  public void deleteResetToken(String resetToken) {
    String key = "resetToken:" + resetToken;
    if (redisTemplate.hasKey(key)) {
      redisTemplate.delete(key);
    }
  }

  /* ========== Signup validation ========== */
  public void markSignupVerified(String email) {
    String key = "signupVerified:" + email;
    redisTemplate.opsForValue().set(key, "true", 10, TimeUnit.MINUTES);
  }

  public boolean isSignupVerified(String email) {
    return redisTemplate.hasKey("signupVerified:" + email);
  }

  public void consumeSignupVerification(String email) {
    redisTemplate.delete("signupVerified:" + email);
  }
}
