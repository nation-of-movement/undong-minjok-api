package com.undongminjok.api.global.logging.exception;

import static org.hibernate.internal.util.ExceptionHelper.getRootCause;

import com.undongminjok.api.global.dto.ApiResponse;
import com.undongminjok.api.global.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
      DataIntegrityViolationException e) {

    Throwable root = getRootCause(e);
    String msg = root.getMessage() != null ? root.getMessage().toLowerCase() : "";

    // 메시지 기반 판단 (가장 확실)
    if (msg.contains("duplicate entry")) {

      // 닉네임 UNIQUE
      if (msg.contains("nickname") || msg.contains("uk2ty1xmrrgtn89xt7kyxx6ta7h")) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.failure("NICKNAME_DUPLICATED", "이미 사용 중인 닉네임입니다."));
      }

      // 이메일 UNIQUE
      if (msg.contains("email")) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.failure("EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다."));
      }

      // 로그인ID UNIQUE
      if (msg.contains("login")) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.failure("LOGIN_ID_DUPLICATED", "이미 사용 중인 아이디입니다."));
      }
    }

    log.error("[DATA INTEGRITY ERROR]", e);
    return ResponseEntity.badRequest()
        .body(ApiResponse.failure("DATA_INTEGRITY_ERROR", "데이터 무결성 오류가 발생했습니다."));
  }


  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
    log.error("[BUSINESS ERROR] code={} message={}", ex.getErrorCode(), ex.getMessage());
    return ResponseEntity.status(ex.getErrorCode().getHttpStatusCode())
        .body(ApiResponse.failure(ex.getErrorCode().getErrorCode(), ex.getMessage()));
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ApiResponse<?>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
    log.error("[ACCESS DENIED ERROR] message={}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)  // 403 Forbidden
                         .body(ApiResponse.failure("ACCESS_DENIED", "접근 권한이 없습니다."));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
    log.error("[SYSTEM ERROR]", ex);
    return ResponseEntity.internalServerError()
        .body(ApiResponse.failure("SYSTEM_ERROR", "SERVER_ERROR"));
  }
}
