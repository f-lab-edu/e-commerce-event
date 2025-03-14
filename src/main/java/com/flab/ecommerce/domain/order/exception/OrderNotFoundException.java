package com.flab.ecommerce.domain.order.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException() {
        super("해당 주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND");
    }
}
