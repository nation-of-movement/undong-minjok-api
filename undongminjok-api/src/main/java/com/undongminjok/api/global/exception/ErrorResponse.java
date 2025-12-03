package com.undongminjok.api.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private String errorCode;
  private String message;

  public ErrorResponse(ErrorCode errorCode) {
    this.errorCode = errorCode.getErrorCode();
    this.message = errorCode.getMessage();
  }
}
