package com.flab.ecommerce.domain.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryCreateRequestDTO {
    @NotBlank(message = "카테고리 이름은 필수 입력 값입니다.")
    private String name;

    private Long parentCategoryId;
}
