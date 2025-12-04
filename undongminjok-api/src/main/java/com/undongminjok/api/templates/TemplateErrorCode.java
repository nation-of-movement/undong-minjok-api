package com.undongminjok.api.templates;

import com.undongminjok.api.global.exception.ErrorCode;
import com.undongminjok.api.templates.domain.Template;
import org.springframework.http.HttpStatus;

public enum TemplateErrorCode implements ErrorCode{
  TEMPLATE_NOT_FOUND("TEMPLATE_001", "해당 템플릿을 찾을 수 없습니다.", HttpStatus.NOT_FOUND) ;

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;

  TemplateErrorCode(String errorCode, String message, HttpStatus httpStatus) {
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
