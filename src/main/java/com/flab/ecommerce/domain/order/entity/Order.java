package com.flab.ecommerce.domain.order.entity;

import com.flab.ecommerce.domain.common.ShippingAddress;
import com.flab.ecommerce.domain.order.enums.OrderStatus;
import com.flab.ecommerce.domain.order.exception.OrderAlreadyShippedException;
import com.flab.ecommerce.domain.order.exception.OrderPermissionDeniedException;
import com.flab.ecommerce.domain.order.exception.OrderStatusChangeNotAllowedException;
import com.flab.ecommerce.domain.product.entity.Product;
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

    @Embedded
    private ShippingAddress shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(Long memberId, BigDecimal totalPrice, ShippingAddress shippingAddress) {
        this.memberId = memberId;
        this.totalPrice = totalPrice;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PENDING;
    }

    public void addOrderItem(Product product, int quantity) {
        this.orderItems.add(new OrderItem(this, product, quantity));
    }

    public void cancel(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new OrderPermissionDeniedException();
        }

        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new OrderAlreadyShippedException();
        }

        this.status = OrderStatus.CANCELED;
    }

    public void updateStatus(OrderStatus status) {
        if (this.status == OrderStatus.CANCELED || this.status == OrderStatus.DELIVERED) {
            throw new OrderStatusChangeNotAllowedException();
        }

        this.status = status;
    }

    public void completeOrder() {
        this.status = OrderStatus.PAID;
    }
}
