package com.flab.ecommerce.global.exception;

import com.flab.ecommerce.global.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    protected BaseException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(status.value(), getMessage(), errorCode);
    }
}
