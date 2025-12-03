package com.undongminjok.api.global.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiResponse<T> {

  private boolean success;         // 요청 성공 여부
  private T data;                  // 실제 데이터 (성공 시만 사용)
  private String errorCode;        // 실패 시 에러 코드
  private String message;          // 실패 시 에러 메세지
  private LocalDateTime timestamp; // 응답 생성 시간

  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
                      .success(true)
                      .data(data)
                      .timestamp(LocalDateTime.now())
                      .build();
  }

  public static <T> ApiResponse<T> failure(String errorCode, String message) {
    return ApiResponse.<T>builder()
                      .success(false)
                      .errorCode(errorCode)
                      .message(message)
                      .timestamp(LocalDateTime.now())
                      .build();
  }
}