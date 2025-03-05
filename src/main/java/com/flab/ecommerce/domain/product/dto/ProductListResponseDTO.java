package com.flab.ecommerce.domain.product.dto;

import com.flab.ecommerce.domain.product.entity.Product;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductListResponseDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stockQuantity;
    private String imageUrl;

    public ProductListResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.imageUrl = product.getImageUrl();
    }
}