package com.flab.ecommerce.domain.product.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OutOfStockException extends BaseException {

    public OutOfStockException(long productId, int stock, int requestQuantity) {
        super(String.format("상품(ID: %d)의 재고가 부족합니다. (재고: %d, 요청 수량: %d)", productId, stock, requestQuantity),
                HttpStatus.BAD_REQUEST, "OUT_OF_STOCK");
    }
}
