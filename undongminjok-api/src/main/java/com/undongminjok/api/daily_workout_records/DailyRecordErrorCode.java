package com.undongminjok.api.daily_workout_records;

import com.undongminjok.api.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum DailyRecordErrorCode implements ErrorCode {

  WORKOUT_RECORD_NOT_FOUND("WORKOUT_001", "운동 기록을 찾을 수 없습니다. ", HttpStatus.NOT_FOUND),
  WORKOUT_EXERCISE_EMPTY("WORKOUT_002", "Workout exercises cannot be empty", HttpStatus.BAD_REQUEST),
  EMPTY_EXERCISE_LIST("WORKOUT_003", "최소 한 개 이상의 값을 입력해야합니다. ", HttpStatus.BAD_REQUEST),
  INVALID_EQUIPMENT_ID("WORKOUT_004", "잘못된 운동기구 선택입니다.", HttpStatus.BAD_REQUEST),
  RECORD_NOT_FOUND("WORKOUT_005", "운동 기록을 찾을 수 없습니다", HttpStatus.NOT_FOUND);

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;

  DailyRecordErrorCode(String errorCode, String message, HttpStatus httpStatus) {
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
