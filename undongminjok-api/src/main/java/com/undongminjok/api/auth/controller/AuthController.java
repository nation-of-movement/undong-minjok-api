package com.undongminjok.api.auth.controller;

import com.undongminjok.api.auth.dto.AccessTokenResponse;
import com.undongminjok.api.auth.dto.EmailRequest;
import com.undongminjok.api.auth.dto.LoginRequest;
import com.undongminjok.api.auth.dto.TokenResponse;
import com.undongminjok.api.auth.dto.VerificationCodeRequest;
import com.undongminjok.api.auth.dto.VerificationCodeResponse;
import com.undongminjok.api.auth.service.AuthService;
import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.global.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Auth",
    description = "로그인, 로그아웃, 토큰 재발급 및 이메일 인증 API"
)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;
  private static final String COOKIE_NAME = "refreshToken";

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AccessTokenResponse>> login(
      @RequestBody LoginRequest request
  ) {
    TokenResponse response = authService.login(request);
    log.info("TEST LOGBACK");
    ResponseCookie refreshCookie = ResponseCookie.from(COOKIE_NAME, response.getRefreshToken())
        .httpOnly(true)
        .secure(false)   // 개발 환경에서는 false, 운영에서는 true
        .path("/")
        .maxAge(
            jwtTokenProvider.getRefreshExpiration() / 1000)
        .sameSite("Lax")
        .build();

    AccessTokenResponse accessTokenResponse = AccessTokenResponse.builder()
        .accessToken(
            response.getAccessToken())
        .build();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .body(ApiResponse.success(accessTokenResponse));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestHeader("Authorization") String authorizationHeader
  ) {
    authService.logout(authorizationHeader);

    ResponseCookie deleteCookie = ResponseCookie.from(COOKIE_NAME, "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(0)     // 즉시 만료
        .sameSite("Lax")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(ApiResponse.success(null));
  }

  @PostMapping("/token-reissue")
  public ResponseEntity<ApiResponse<AccessTokenResponse>> tokenReissue(
      @CookieValue(name = COOKIE_NAME, required = false) String refreshToken
  ) {

    AccessTokenResponse newAccessToken = authService.tokenReissue(
        refreshToken
    );

    ResponseCookie refreshCookie = ResponseCookie.from(COOKIE_NAME, refreshToken)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(
            jwtTokenProvider.getRefreshExpiration() / 1000)
        .sameSite("Lax")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .body(ApiResponse.success(newAccessToken));
  }

  /**
   * 본인인증 - 이메일로 인증번호 보내기
   *
   * @param request
   * @return
   */
  @PostMapping("/email")
  public ResponseEntity<ApiResponse<?>> sendVerificationCode(
      @RequestBody EmailRequest request) {
    authService.sendVerificationCode(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  /**
   * 인증번호가 같은지 확인
   *
   * @param request
   * @return
   */
  @PostMapping("/code")
  public ResponseEntity<ApiResponse<VerificationCodeResponse>> existVerificationCode(
      @RequestBody VerificationCodeRequest request) {

    VerificationCodeResponse response = authService.existVerificationCode(request);

    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
