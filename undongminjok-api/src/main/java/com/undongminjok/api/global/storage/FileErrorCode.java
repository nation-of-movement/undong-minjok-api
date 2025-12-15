package com.undongminjok.api.global.storage;

import com.undongminjok.api.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum FileErrorCode implements ErrorCode {
  FILE_DIR_CREATE_FAILED("FILE_001", "file don`t created", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_EMPTY("FILE_002", "file empty", HttpStatus.BAD_REQUEST),
  FILE_NAME_NOT_PRESENT("FILE_003", "file name not present", HttpStatus.NOT_FOUND),
  FILE_EXTENSION_NOT_ALLOWED("FILE_004", "file extension not allowed", HttpStatus.BAD_REQUEST),
  FILE_PATH_TRAVERSAL_DETECTED("FILE_005", "file path traversal", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_DELETE_FAILED("FILE_006", "file delete failed", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_DELETE_IO_ERROR("FILE_007", "file delete io error", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_SAVE_IO_ERROR("FILE_008", "file save io error", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String errorCode;
  private final String message;
  private final HttpStatusCode httpStatusCode;

  FileErrorCode(String errorCode, String message, HttpStatusCode httpStatusCode) {
    this.errorCode = errorCode;
    this.message = message;
    this.httpStatusCode = httpStatusCode;
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
  public HttpStatusCode getHttpStatusCode() {
    return httpStatusCode;
  }
}
