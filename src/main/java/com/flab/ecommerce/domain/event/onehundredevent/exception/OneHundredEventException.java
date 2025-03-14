package com.flab.ecommerce.domain.event.onehundredevent.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OneHundredEventException extends BaseException {

    public OneHundredEventException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }

    public static class AlreadyInQueueException extends OneHundredEventException {
        public AlreadyInQueueException() {
            super("이미 대기열에 등록된 사용자입니다.", "ALREADY_IN_QUEUE");
        }
    }

    public static class AlreadyParticipatedException extends OneHundredEventException {
        public AlreadyParticipatedException() {
            super("이미 참가한 이벤트입니다.", "ALREADY_PARTICIPATED");
        }
    }

    public static class NotInPendingStateException extends OneHundredEventException {
        public NotInPendingStateException() {
            super("결제 대기 상태가 아닙니다.", "NOT_IN_PENDING_STATE");
        }
    }
}
