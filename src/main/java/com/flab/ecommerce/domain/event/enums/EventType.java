package com.flab.ecommerce.domain.event.enums;

import com.flab.ecommerce.domain.event.exception.UnknownEventTypeException;

import java.util.Arrays;

public enum EventType {
    ONE_HUNDRED_WON_DEAL("100WD");

    private final String code;

    EventType(String code) {
        this.code = code;
    }

    public static EventType fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(UnknownEventTypeException::new);
    }
}
