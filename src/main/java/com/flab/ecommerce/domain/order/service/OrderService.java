package com.flab.ecommerce.domain.order.service;

import com.flab.ecommerce.domain.cart.dto.CartItemResponseDTO;
import com.flab.ecommerce.domain.cart.dto.CartResponseDTO;
import com.flab.ecommerce.domain.cart.exception.CartEmptyException;
import com.flab.ecommerce.domain.cart.service.CartService;
import com.flab.ecommerce.domain.order.dto.OrderResponseDTO;
import com.flab.ecommerce.domain.order.entity.Order;
import com.flab.ecommerce.domain.order.entity.OrderItem;
import com.flab.ecommerce.domain.order.repository.OrderRepository;
import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.service.ProductService;
import com.flab.ecommerce.domain.product.service.ProductStatusChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ProductStatusChecker statusChecker;
    private final CartService cartService;

    @Transactional
    public OrderResponseDTO placeOrder(Long memberId) {
        CartResponseDTO cart = cartService.getCart(memberId);

        if(cart.getItems().isEmpty()) {
            throw new CartEmptyException();
        }

        Order order = new Order(memberId, cart.getTotalPrice());
        for(CartItemResponseDTO cartItem : cart.getItems()) {
            Product product = productService.getProductOrThrow(cartItem.getProductId());
            order.getOrderItems().add(new OrderItem(order, product, cartItem.getQuantity()));
        }

        orderRepository.save(order);
        cartService.clearCart(memberId);

        return new OrderResponseDTO(order);

    }
}
