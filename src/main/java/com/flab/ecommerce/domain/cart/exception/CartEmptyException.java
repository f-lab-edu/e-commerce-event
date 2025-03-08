package com.flab.ecommerce.domain.cart.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CartEmptyException extends BaseException {
    public CartEmptyException() {
        super("장바구니가 비어있습니다.", HttpStatus.BAD_REQUEST, "CART_EMPTY");
    }
}
