package com.flab.ecommerce.domain.event.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EventNotFoundException extends BaseException {

    public EventNotFoundException() {super ("이벤트를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST, "EVENT_NOT_FOUND" );}
}
