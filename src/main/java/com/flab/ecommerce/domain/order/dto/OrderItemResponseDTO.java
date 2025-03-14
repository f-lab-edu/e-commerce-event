package com.flab.ecommerce.domain.order.dto;

import com.flab.ecommerce.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private int quantity;

    public OrderItemResponseDTO(OrderItem item) {
        this.productId = item.getProductId();
        this.productName = item.getProductName();
        this.productPrice = item.getProductPrice();
        this.quantity = item.getQuantity();
    }
}
