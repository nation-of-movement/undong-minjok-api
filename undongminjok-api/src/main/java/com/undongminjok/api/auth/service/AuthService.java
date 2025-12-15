package com.undongminjok.api.auth.service;

import com.undongminjok.api.auth.AuthErrorCode;
import com.undongminjok.api.auth.dto.AccessTokenResponse;
import com.undongminjok.api.auth.dto.EmailRequest;
import com.undongminjok.api.auth.dto.LoginRequest;
import com.undongminjok.api.auth.dto.TokenResponse;
import com.undongminjok.api.auth.dto.VerificationCodeRequest;
import com.undongminjok.api.auth.dto.VerificationCodeResponse;
import com.undongminjok.api.global.domain.MailType;
import com.undongminjok.api.global.dto.LoginUserInfo;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.security.jwt.JwtTokenProvider;
import com.undongminjok.api.global.util.AuthRedisService;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.global.util.mail.EmailVerificationCode;
import com.undongminjok.api.global.util.mail.MailService;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.domain.UserStatus;
import com.undongminjok.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthRedisService authRedisService;
  private final MailService mailService;

  @Transactional
  public TokenResponse login(LoginRequest request) {

    User user = userRepository.findByLoginId(request.getLoginId())
        .orElseThrow(
            () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    UserStatus status = user.getStatus();

    if (status == UserStatus.WITHDRAW) {
      throw new BusinessException(UserErrorCode.USER_CANCELED);
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BusinessException(UserErrorCode.USER_INVALID_PASSWORD);
    }

    String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getRole());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId(), user.getRole());

    authRedisService.saveRefreshToken(user.getLoginId(), refreshToken);

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Transactional
  public void logout(String authorizationHeader) {

    String accessToken = jwtTokenProvider.resolveToken(authorizationHeader);

    LoginUserInfo user = SecurityUtil.getLoginUserInfo();

    Boolean exist = authRedisService.existRefreshTokenByLoginId(user.getLoginId());
    if (Boolean.FALSE.equals(exist)) {
      throw new BusinessException(AuthErrorCode.INVALID_VERIFICATION_TOKEN);
    }

    long remainingTime = jwtTokenProvider.getRemainingTime(accessToken);

    authRedisService.deleteRefreshTokenByLoginId(user.getLoginId());

    authRedisService.addBlackListAccessToken(accessToken, remainingTime);
  }

  @Transactional
  public AccessTokenResponse tokenReissue(String refreshToken) {

    if (refreshToken == null) {
      throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    // RefreshToken 검증
    jwtTokenProvider.validateToken(refreshToken);

    // RefreshToken이 Redis에 존재하는지 확인
    String loginId = jwtTokenProvider.getLoginIdFromJWT(refreshToken);

    Boolean exists = authRedisService.existRefreshTokenByLoginId(loginId);
    if (Boolean.FALSE.equals(exists)) {
      throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    // 새 AccessToken 생성 (RefreshToken은 재사용)
    String newAccessToken =
        jwtTokenProvider.createAccessToken(
            loginId,
            jwtTokenProvider.getUserRoleFromJWT(refreshToken)
        );

    // Body에는 AccessToken만 내려줌
    return AccessTokenResponse.builder()
        .accessToken(newAccessToken)
        .build();
  }

  public void sendVerificationCode(EmailRequest request) {

    validateEmailRequest(request);

    switch (request.getPurpose()) {
      case SIGNUP -> validateForSignup(request.getEmail());

      case ID_SEARCH, PASSWORD_SEARCH -> validateForSearch(request.getEmail());

      case PASSWORD_RESET -> validateForPasswordReset(request.getEmail());
    }

    String code = EmailVerificationCode.getCode();

    authRedisService.saveVerificationCode(request, code);

    mailService.sendMail(request, MailType.VERIFICATION, code);
  }

  private void validateForSignup(String email) {
    boolean exists = userRepository.existsByEmail(email);
    if (exists) {
      throw new BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
    }
  }

  public VerificationCodeResponse existVerificationCode(VerificationCodeRequest request) {

    Boolean exist = authRedisService.existVerificationCode(request);

    if (Boolean.FALSE.equals(exist)) {
      throw new BusinessException(AuthErrorCode.INVALID_VERIFICATION_TOKEN);
    }

    // 인증코드 1회용 → 즉시 삭제
    authRedisService.deleteKeyEmail(request);

    return switch (request.getPurpose()) {
      case SIGNUP -> {
        authRedisService.markSignupVerified(request.getEmail());
        yield VerificationCodeResponse.builder()
            .success(true)
            .resetToken(null)
            .build();
      }

      case ID_SEARCH, PASSWORD_RESET, PASSWORD_SEARCH -> {
        String resetToken = authRedisService.createAndSaveResetToken(request.getEmail());
        yield VerificationCodeResponse.builder()
            .success(true)
            .resetToken(resetToken)
            .build();
      }

      default -> throw new BusinessException(UserErrorCode.INVALID_PURPOSE);
    };
  }

  private void validateEmailRequest(EmailRequest request) {
    if (request.getEmail() == null || request.getEmail().isBlank()) {
      throw new BusinessException(UserErrorCode.EMAIL_REQUIRED);
    }
    if (request.getPurpose() == null) {
      throw new BusinessException(UserErrorCode.INVALID_PURPOSE);
    }
  }

  private void validateForSearch(String email) {
    userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
  }

  private void validateForPasswordReset(String email) {

    Long userId = SecurityUtil.getLoginUserInfo().getUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.UNAUTHORIZED_USER);
    }

    userRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
  }


}
