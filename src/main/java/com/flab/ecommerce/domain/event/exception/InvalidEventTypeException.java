package com.flab.ecommerce.domain.event.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidEventTypeException extends BaseException {

    public InvalidEventTypeException() {super("해당 이벤트 타입이 아닙니다.", HttpStatus.BAD_REQUEST, "INVALID_EVENT_TYPE");}
}
