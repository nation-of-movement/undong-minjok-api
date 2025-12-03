package com.undongminjok.api.auth.service;

import com.undongminjok.api.auth.dto.LoginRequest;
import com.undongminjok.api.auth.dto.TokenResponse;
import com.undongminjok.api.global.exception.BusinessException;
import com.undongminjok.api.global.security.JwtTokenProvider;
import com.undongminjok.api.global.util.AuthRedisService;
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

  @Transactional
  public TokenResponse login(LoginRequest request) {

    User user = userRepository.findByLoginId(request.getLoginId())
                              .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

    UserStatus status = user.getStatus();

    if (status == UserStatus.WITHDRAW) {
      throw new BusinessException(UserErrorCode.USER_CANCELED);
    }

    if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BusinessException(UserErrorCode.USER_INVALID_PASSWORD);
    }

    String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(),user.getRole());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId(),user.getRole());

    authRedisService.saveRefreshToken(user.getLoginId(), refreshToken);

    return TokenResponse.builder()
                        .accessToken(accessToken)
                        .build();
  }
}
