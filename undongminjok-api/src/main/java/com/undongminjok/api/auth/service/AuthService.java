package com.undongminjok.api.auth.service;

import com.undongminjok.api.auth.AuthErrorCode;
import com.undongminjok.api.auth.dto.AccessTokenResponse;
import com.undongminjok.api.auth.dto.LoginRequest;
import com.undongminjok.api.auth.dto.TokenResponse;
import com.undongminjok.api.global.dto.LoginUserInfo;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.security.jwt.JwtTokenProvider;
import com.undongminjok.api.global.util.AuthRedisService;
import com.undongminjok.api.global.util.SecurityUtil;
import com.undongminjok.api.user.UserErrorCode;
import com.undongminjok.api.user.domain.User;
import com.undongminjok.api.user.domain.UserStatus;
import com.undongminjok.api.user.repository.UserRepository;
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

  public AccessTokenResponse tokenReissue(String authorizationHeader, String refreshToken) {

    if (refreshToken == null) {
      throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    // AccessToken 추출
    String accessToken = jwtTokenProvider.resolveToken(authorizationHeader);

    // AccessToken 만료 여부 확인 (만료 상태여도 재발급 자체는 가능)
    if (jwtTokenProvider.getRemainingTime(accessToken) <= 0) {
      throw new BusinessException(AuthErrorCode.ACCESS_TOKEN_NOT_EXPIRED);
    }

    // 3. RefreshToken 검증
    jwtTokenProvider.validateToken(refreshToken);

    // 4. RefreshToken이 Redis에 존재하는지 확인
    String loginId = jwtTokenProvider.getLoginIdFromJWT(refreshToken);
    Boolean exists = authRedisService.existRefreshTokenByLoginId(loginId);
    if (Boolean.FALSE.equals(exists)) {
      throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    // 5. AccessToken 블랙리스트 여부 체크
    if (authRedisService.isBlacklisted(accessToken)) {
      throw new BusinessException(AuthErrorCode.INVALID_ACCESS_TOKEN);
    }

    // 6. 새 AccessToken 생성 (RefreshToken은 재사용)
    String newAccessToken =
        jwtTokenProvider.createAccessToken(
            loginId,
            jwtTokenProvider.getUserRoleFromJWT(refreshToken)
        );

    // 7. Body에는 AccessToken만 내려줌
    return AccessTokenResponse.builder()
                              .accessToken(newAccessToken)
                              .build();
  }
}
