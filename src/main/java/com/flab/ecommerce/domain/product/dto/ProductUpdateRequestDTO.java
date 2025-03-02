package com.flab.ecommerce.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String imageUrl;
    private Long categoryId;
}
