package com.undongminjok.api.point;

import com.undongminjok.api.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PointErrorCode implements ErrorCode {
  POINT_HISTORY_NOT_FOUND("POINT_001", "해당 포인트 이력을 찾을 수 없습니다.", HttpStatus.NOT_FOUND) ;

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;

  PointErrorCode(String errorCode, String message, HttpStatus httpStatus) {
    this.errorCode = errorCode;
    this.message = message;
    this.httpStatus = httpStatus;
  }
  @Override
  public String getErrorCode() {
    return errorCode;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public HttpStatus getHttpStatusCode() {
    return httpStatus;
  }
}
