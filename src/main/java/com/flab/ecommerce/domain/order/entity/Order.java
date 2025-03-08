package com.flab.ecommerce.domain.order.entity;

import com.flab.ecommerce.domain.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Entity
@Getter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(Long memberId, BigDecimal totalPrice) {
        this.memberId = memberId;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.PENDING;
    }

    public void completeOrder() {
        this.status = OrderStatus.PAID;
    }
}
