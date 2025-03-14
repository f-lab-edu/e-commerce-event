package com.flab.ecommerce.domain.event.onehundredevent.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AlreadyInQueueException extends BaseException {

    public AlreadyInQueueException() {
        super("이미 대기열에 등록된 사용자입니다.", HttpStatus.BAD_REQUEST, "ALREADY_IN_QUEUE");
    }
}
