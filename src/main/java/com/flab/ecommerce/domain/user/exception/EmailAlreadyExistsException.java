package com.flab.ecommerce.domain.user.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BaseException {
    public EmailAlreadyExistsException() {
        super("이미 가입된 이메일입니다.", HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");
    }
}
