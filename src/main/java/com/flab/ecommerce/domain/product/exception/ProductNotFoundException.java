package com.flab.ecommerce.domain.product.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseException {

    public ProductNotFoundException(long id) {
        super("해당 상품이 존재하지 않습니다. id=" + id, HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
    }
}
