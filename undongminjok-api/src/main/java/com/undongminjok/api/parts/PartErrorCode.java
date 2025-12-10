package com.undongminjok.api.parts;

import com.undongminjok.api.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PartErrorCode implements ErrorCode {

    PART_NOT_FOUND("PART_001", "해당 부위를 찾을 수 없습니다. ", HttpStatus.NOT_FOUND);


    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    PartErrorCode(String errorCode, String message, HttpStatus httpStatus) {
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
