package com.flab.ecommerce.domain.order.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OrderAlreadyShippedException extends BaseException {
    public OrderAlreadyShippedException() {
        super("배송 중이거나 배송 완료된 주문은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST, "ORDER_CANCELLATION_NOT_ALLOWED");
    }
}
