package com.flab.ecommerce.domain.event.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;
public class EventAlreadyEndedException extends BaseException {
    public EventAlreadyEndedException() {
        super("이미 종료된 이벤트입니다.", HttpStatus.BAD_REQUEST, "EVENT_ALREADY_ENDED");
    }
}

