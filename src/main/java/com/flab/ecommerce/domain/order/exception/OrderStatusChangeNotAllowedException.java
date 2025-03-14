package com.flab.ecommerce.domain.order.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OrderStatusChangeNotAllowedException extends BaseException {
    public OrderStatusChangeNotAllowedException() {
        super("해당 주문 상태로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST, "ORDER_STATUS_CHANGE_NOT_ALLOWED");
    }
}
