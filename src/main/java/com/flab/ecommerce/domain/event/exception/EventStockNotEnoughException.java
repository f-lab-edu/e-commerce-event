package com.flab.ecommerce.domain.event.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EventStockNotEnoughException extends BaseException {
    public EventStockNotEnoughException() {super ("이벤트 상품 재고가 부족합니다.", HttpStatus.BAD_REQUEST, "EVENT_STOCK_NOT_ENOUGH" );}
}
