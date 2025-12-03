package com.undongminjok.api.daily_workout_records;

import com.undongminjok.api.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum DailyRecordErrorCode implements ErrorCode {

  WORKOUT_RECORD_NOT_FOUND("WORKOUT_001", "Workout record not found", HttpStatus.NOT_FOUND),
  WORKOUT_EXERCISE_EMPTY("WORKOUT_002", "Workout exercises cannot be empty",
      HttpStatus.BAD_REQUEST);

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
