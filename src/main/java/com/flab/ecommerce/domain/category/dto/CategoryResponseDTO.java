package com.flab.ecommerce.domain.category.dto;

import com.flab.ecommerce.domain.category.entity.Category;
import lombok.Getter;

@Getter
public class CategoryResponseDTO {
    private final Long id;
    private final String name;
    private final Long parentCategoryId;
    private final boolean isActive;

    public CategoryResponseDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.parentCategoryId = category.getParentCategory() == null ? null : category.getParentCategory().getId();
        this.isActive = category.isActive();
    }
}
