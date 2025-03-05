package com.flab.ecommerce.domain.category.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {

    public CategoryNotFoundException() {
        super("존재하지 않는 카테고리입니다.", HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND");
    }
}

