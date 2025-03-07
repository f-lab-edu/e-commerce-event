package com.flab.ecommerce.domain.cart.dto;

import lombok.Getter;

@Getter
public class CartItemAddRequestDTO {

    private Long productId;
    private int quantity;
}
