package com.flab.ecommerce.domain.cart.dto;

import com.flab.ecommerce.domain.cart.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponseDTO {
    private List<CartItemResponseDTO> items;
    private BigDecimal totalPrice;

    public CartResponseDTO(Cart cart) {
        this.items = cart.getItems().stream()
                .map(CartItemResponseDTO::new)
                .toList();
        this.totalPrice = cart.getTotalPrice();
    }
}
