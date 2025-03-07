package com.flab.ecommerce.domain.event.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UnknownEventTypeException extends BaseException {
    public UnknownEventTypeException() { super("알 수 없는 이벤트 타입입니다.", HttpStatus.BAD_REQUEST, "UNKNOWN_EVENT_TYPE");}
}
