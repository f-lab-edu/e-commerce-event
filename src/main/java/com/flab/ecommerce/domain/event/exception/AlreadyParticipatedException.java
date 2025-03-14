package com.flab.ecommerce.domain.event.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AlreadyParticipatedException extends BaseException {

    public AlreadyParticipatedException() {
        super("이미 참가한 이벤트입니다.", HttpStatus.BAD_REQUEST, "ALREADY_PARTICIPATED");
    }
}
