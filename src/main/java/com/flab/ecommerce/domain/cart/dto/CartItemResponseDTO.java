package com.flab.ecommerce.domain.cart.dto;

import com.flab.ecommerce.domain.cart.entity.CartItem;
import lombok.Getter;

@Getter
public class CartItemResponseDTO {
    private Long productId;
    private int quantity;

    public CartItemResponseDTO(CartItem item) {
        this.productId = item.getProduct().getId();
        this.quantity = item.getQuantity();
    }
}
