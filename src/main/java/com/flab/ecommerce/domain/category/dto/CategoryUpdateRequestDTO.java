package com.flab.ecommerce.domain.category.dto;

import lombok.Getter;

@Getter
public class CategoryUpdateRequestDTO {

    private String name;

    private Long parentCategoryId;
}
