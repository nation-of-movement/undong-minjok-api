package com.undongminjok.api.auth.service;

import com.undongminjok.api.auth.AuthErrorCode;
import com.undongminjok.api.auth.domain.VerificationPurpose;
import com.undongminjok.api.auth.dto.AccessTokenResponse;
import com.undongminjok.api.auth.dto.EmailRequest;
import com.undongminjok.api.auth.dto.LoginRequest;
import com.undongminjok.api.auth.dto.ResetPasswordRequest;
import com.undongminjok.api.auth.dto.TokenResponse;
import com.undongminjok.api.auth.dto.VerificationCodeRequest;
import com.undongminjok.api.auth.dto.VerificationCodeResponse;
import com.undongminjok.api.global.domain.MailType;
import com.undongminjok.api.global.dto.LoginUserInfo;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.security.jwt.JwtTokenProvider;
import com.undongminjok.api.global.util.AuthRedisService;
import com.undongminjok.api.global.util.MailService;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.domain.UserStatus;
import com.undongminjok.api.user.repository.UserRepository;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

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

    String refreshToken = authRedisService.findKeyByLoginId(user.getLoginId());

    long remainingTime = jwtTokenProvider.getRemainingTime(refreshToken);

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

  @Transactional
  public void sendVerificationCode(EmailRequest request) {

    if (request.getPurpose() == VerificationPurpose.PASSWORD_RESET) {
      userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    MailType mailType = MailType.VERIFICATION;

    String code = EmailVerificationCode.getCode();

    authRedisService.saveVerificationCode(request, code);

    mailService.sendMail(request, mailType, code);

  }

  @Transactional
  public VerificationCodeResponse existVerificationCode(VerificationCodeRequest request) {

    Boolean exist = authRedisService.existVerificationCode(
        request);

    if (Boolean.FALSE.equals(exist)) {
      throw new BusinessException(AuthErrorCode.INVALID_VERIFICATION_TOKEN);
    }

    authRedisService.deleteKeyEmail(request);

    return switch (request.getPurpose()) {
      case SIGNUP -> VerificationCodeResponse.builder()
                                             .success(true)
                                             .resetToken(null)
                                             .build();
      case PASSWORD_RESET -> {
        String resetToken = authRedisService.createAndSaveResetToken(request.getEmail());
        yield VerificationCodeResponse.builder()
                                      .success(true)
                                      .resetToken(resetToken)
                                      .build();
      }
    };


  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    // resetToken 으로 이메일 조회 + 유효성 검증
    String email = authRedisService.getEmailByResetToken(request.getResetToken());

    // 이메일로 유저 조회
    User user = userRepository.findByEmail(email)
                              .orElseThrow(
                                  () -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    // 비밀번호 변경
    user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

    // 토큰 1회용 처리 (삭제)
    authRedisService.deleteResetToken(request.getResetToken());
  }
}
