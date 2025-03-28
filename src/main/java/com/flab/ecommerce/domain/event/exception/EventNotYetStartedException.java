package com.flab.ecommerce.domain.event.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EventNotYetStartedException extends BaseException {
    public EventNotYetStartedException() {
        super("아직 시작되지 않은 이벤트입니다.", HttpStatus.BAD_REQUEST, "EVENT_NOT_STARTED");
    }
}
