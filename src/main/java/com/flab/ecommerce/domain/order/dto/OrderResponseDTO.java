package com.flab.ecommerce.domain.order.dto;

import com.flab.ecommerce.domain.common.ShippingAddress;
import com.flab.ecommerce.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private BigDecimal totalPrice;
    private String status;
    private ShippingAddress shippingAddress;
    private List<OrderItemResponseDTO> items;

    public OrderResponseDTO(Order order) {
        this.orderId = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus().name();
        this.shippingAddress = order.getShippingAddress();
        this.items = order.getOrderItems().stream()
            .map(OrderItemResponseDTO::new)
                .toList();

    }
}
