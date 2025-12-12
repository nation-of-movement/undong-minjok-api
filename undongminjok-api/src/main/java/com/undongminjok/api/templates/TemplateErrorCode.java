package com.undongminjok.api.templates;

import com.undongminjok.api.global.exception.ErrorCode;
import com.undongminjok.api.templates.domain.Template;
import org.springframework.http.HttpStatus;

public enum TemplateErrorCode implements ErrorCode{
  TEMPLATE_NOT_FOUND("TEMPLATE_001", "해당 템플릿을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  TEMPLATE_HAS_NO_EXERCISES("TEMPLATE_002", "해당 템플릿에 운동 내용이 없습니다", HttpStatus.NOT_FOUND),
  TEMPLATE_CANNOT_DELETE_SOLD("TEMPLATE_003","해당 템플릿의 주인이 아님으로 삭제할 수 없습니다", HttpStatus.BAD_REQUEST),
  TEMPLATE_ALREADY_PURCHASED("TEMPLATE_004", "이미 구매한 템플릿입니다.", HttpStatus.BAD_REQUEST),
  TEMPLATE_SELF_PURCHASE_NOT_ALLOWED("TEMPLATE_005", "본인이 만든 템플릿은 구매할 수 없습니다.", HttpStatus.BAD_REQUEST),
  TEMPLATE_UPDATE_FORBIDDEN("TEMPLATE_006", "템플릿을 수정할 수 있는 권한이 없습니다." , HttpStatus.FORBIDDEN),
  TEMPLATE_EXERCISE_NOT_FOUND("TEMPATE_007", "해당 운동은 존재하지 않는 운동입니다.", HttpStatus.NOT_FOUND);

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
