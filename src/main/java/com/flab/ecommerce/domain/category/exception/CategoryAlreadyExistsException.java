package com.flab.ecommerce.domain.category.exception;

import com.flab.ecommerce.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryAlreadyExistsException extends BaseException {
    public CategoryAlreadyExistsException() {
        super("이미 존재하는 카테고리 이름입니다.", HttpStatus.CONFLICT, "CATEGORY_ALREADY_EXISTS");
    }
}
