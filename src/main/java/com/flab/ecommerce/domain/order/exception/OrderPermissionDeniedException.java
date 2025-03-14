package com.flab.ecommerce.domain.order.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OrderPermissionDeniedException extends BaseException {
    public OrderPermissionDeniedException() {
        super("해당 주문에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN, "ORDER_CANCELLATION_NOT_ALLOWED");
    }
}
