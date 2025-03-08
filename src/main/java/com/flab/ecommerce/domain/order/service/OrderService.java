package com.flab.ecommerce.domain.order.service;

import com.flab.ecommerce.domain.cart.dto.CartResponseDTO;
import com.flab.ecommerce.domain.cart.exception.CartEmptyException;
import com.flab.ecommerce.domain.cart.service.CartService;
import com.flab.ecommerce.domain.common.Address;
import com.flab.ecommerce.domain.common.ShippingAddress;
import com.flab.ecommerce.domain.member.entity.Member;
import com.flab.ecommerce.domain.member.exception.MemberNotFoundException;
import com.flab.ecommerce.domain.member.repository.MemberRepository;
import com.flab.ecommerce.domain.order.dto.OrderRequestDTO;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ProductStatusChecker statusChecker;
    private final MemberRepository memberRepository;
    private final CartService cartService;

    @Transactional
    public OrderResponseDTO placeOrder(Long memberId, OrderRequestDTO request) {
        CartResponseDTO cart = cartService.getCart(memberId);

        if (cart.getItems().isEmpty()) {
            throw new CartEmptyException();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        ShippingAddress shippingAddress = Optional.ofNullable(request)
                .map(OrderRequestDTO::getShippingAddress)
                .orElse(new ShippingAddress(
                        member.getName(),
                        member.getPhoneNumber(),
                        member.getAddress().getZipCode(),
                        member.getAddress().getBaseAddress(),
                        member.getAddress().getDetailAddress()
                ));

        Order order = new Order(memberId, cart.getTotalPrice(), shippingAddress);

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = productService.getProductOrThrow(cartItem.getProductId());
                    return new OrderItem(order, product, cartItem.getQuantity());
                })
                .toList();

        order.addOrderItems(orderItems);

        orderRepository.save(order);
        cartService.clearCart(memberId);

        return new OrderResponseDTO(order);
    }
}
