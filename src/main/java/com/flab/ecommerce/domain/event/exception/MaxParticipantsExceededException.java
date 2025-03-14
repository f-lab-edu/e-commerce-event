package com.flab.ecommerce.domain.event.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MaxParticipantsExceededException extends BaseException {
    public MaxParticipantsExceededException() {
        super("참가자 제한을 초과했습니다.", HttpStatus.BAD_REQUEST, "MAX_PARTICIPANTS_EXCEEDED");
    }
}
