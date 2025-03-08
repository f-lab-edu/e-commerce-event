package com.flab.ecommerce.domain.order.entity;

import com.flab.ecommerce.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private int quantity;

    public OrderItem(Order order, Product product, int quantity) {
        this.order = order;
        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.quantity = quantity;
    }
}
