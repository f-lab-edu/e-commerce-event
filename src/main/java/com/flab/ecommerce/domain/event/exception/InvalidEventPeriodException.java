package com.flab.ecommerce.domain.event.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class InvalidEventPeriodException extends BaseException {
    public InvalidEventPeriodException(LocalDateTime startDate, LocalDateTime endDate) {
        super("이벤트 종료일은 시작일 이후여야 합니다. 시작일: " + startDate + ", 종료일: " + endDate,
                HttpStatus.BAD_REQUEST,
                "INVALID_EVENT_PERIOD");
    }
}
