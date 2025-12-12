package com.undongminjok.api.payments;

import com.undongminjok.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
public enum PaymentsErrorCode implements ErrorCode {
  PAYMENTS_ERROR_CODE("POINT_001", "결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND) ;


  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;



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
