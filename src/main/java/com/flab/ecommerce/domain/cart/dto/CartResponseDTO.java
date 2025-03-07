package com.flab.ecommerce.domain.cart.dto;

import com.flab.ecommerce.domain.cart.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class CartResponseDTO {
    private List<CartItemResponseDTO> items;
    private BigDecimal totalPrice;

    public CartResponseDTO(List<CartItemResponseDTO> items, BigDecimal totalPrice) {
        this.items = items;
        this.totalPrice = totalPrice;
    }

}
