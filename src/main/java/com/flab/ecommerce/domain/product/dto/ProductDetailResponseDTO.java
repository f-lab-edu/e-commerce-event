package com.flab.ecommerce.domain.product.dto;

import com.flab.ecommerce.domain.product.entity.Product;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductDetailResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String imageUrl;
    private Long categoryId;

    public ProductDetailResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.imageUrl = product.getImageUrl();
        this.categoryId = product.getCategory().getId();
    }

}
