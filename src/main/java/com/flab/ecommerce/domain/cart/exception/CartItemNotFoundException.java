package com.flab.ecommerce.domain.cart.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CartItemNotFoundException extends BaseException {
    public CartItemNotFoundException(Long cartItemId) {
        super(String.format("장바구니 상품(ID: %d)이 존재하지 않습니다.", cartItemId), HttpStatus.NOT_FOUND, "CART_ITEM_NOT_FOUND");
    }
}
