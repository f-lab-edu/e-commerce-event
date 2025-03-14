package com.flab.ecommerce.domain.cart.dto;

import com.flab.ecommerce.domain.cart.entity.CartItem;
import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CartItemResponseDTO {

    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private int stock;
    private ProductStatus status;

    public static CartItemResponseDTO from(CartItem item, int stock, ProductStatus status) {
        return new CartItemResponseDTO(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getProductPrice(),
                stock,
                status
        );
    }
}





