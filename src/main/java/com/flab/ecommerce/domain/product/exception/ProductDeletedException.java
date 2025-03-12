package com.flab.ecommerce.domain.product.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductDeletedException extends BaseException {

    public ProductDeletedException() {
        super("해당 상품이 삭제되었습니다.", HttpStatus.NOT_FOUND, "PRODUCT_DELETED");
    }
}
