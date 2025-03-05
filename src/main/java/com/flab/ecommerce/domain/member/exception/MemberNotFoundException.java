package com.flab.ecommerce.domain.member.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends BaseException {
    public MemberNotFoundException() {
        super("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }
}

