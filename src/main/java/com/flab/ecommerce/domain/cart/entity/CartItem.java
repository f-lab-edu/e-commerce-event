package com.flab.ecommerce.domain.cart.entity;

import com.flab.ecommerce.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private int quantity;

    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.quantity = quantity;
    }

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }


}
