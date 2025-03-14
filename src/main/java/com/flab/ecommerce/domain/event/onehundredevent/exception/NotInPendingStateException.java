package com.flab.ecommerce.domain.event.onehundredevent.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotInPendingStateException extends BaseException {

    public NotInPendingStateException() {
        super("결제 대기 상태가 아닙니다.", HttpStatus.BAD_REQUEST, "NOT_IN_PENDING_STATE");
    }
}
