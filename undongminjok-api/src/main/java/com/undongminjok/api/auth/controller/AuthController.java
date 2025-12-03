package com.undongminjok.api.auth.controller;

import com.undongminjok.api.auth.dto.TokenResponse;
import com.undongminjok.api.auth.service.AuthService;
import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.auth.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<TokenResponse>> login(
      @RequestBody LoginRequest request
  ) {
    TokenResponse response = authService.login(request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response));
  }
}
