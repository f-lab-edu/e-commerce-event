package com.flab.ecommerce.domain.order.service;

import com.flab.ecommerce.domain.cart.dto.CartResponseDTO;
import com.flab.ecommerce.domain.cart.exception.CartEmptyException;
import com.flab.ecommerce.domain.cart.service.CartService;
import com.flab.ecommerce.domain.common.ShippingAddress;
import com.flab.ecommerce.domain.member.entity.Member;
import com.flab.ecommerce.domain.member.exception.MemberNotFoundException;
import com.flab.ecommerce.domain.member.repository.MemberRepository;
import com.flab.ecommerce.domain.order.dto.OrderRequestDTO;
import com.flab.ecommerce.domain.order.dto.OrderResponseDTO;
import com.flab.ecommerce.domain.order.entity.Order;
import com.flab.ecommerce.domain.order.enums.OrderStatus;
import com.flab.ecommerce.domain.order.exception.OrderPermissionDeniedException;
import com.flab.ecommerce.domain.order.exception.OrderNotFoundException;
import com.flab.ecommerce.domain.order.repository.OrderRepository;
import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.service.ProductService;
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

        cart.getItems().forEach(cartItem -> {
            Product product = productService.getProduct(cartItem.getProductId());
            order.addOrderItem(product, cartItem.getQuantity());
        });

        orderRepository.save(order);
        cartService.clearCart(memberId);

        return new OrderResponseDTO(order);
    }

    public List<OrderResponseDTO> getMyOrders(Long memberId) {
        return orderRepository.findByMemberIdOrderByIdDesc(memberId)
                .stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    public OrderResponseDTO getMyOrderById(Long orderId, Long memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        if(!order.getMemberId().equals(memberId)) {
            throw new OrderPermissionDeniedException();
        }
        return new OrderResponseDTO(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long memberId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        if (!order.getMemberId().equals(memberId)) {
            throw new OrderPermissionDeniedException();
        }
        order.cancel(memberId);
    }

    public List<OrderResponseDTO> getAllOrders(OrderStatus status) {
        return orderRepository.findAll().stream()
                .filter(order -> status == null || order.getStatus() == status)
                .map(OrderResponseDTO::new)
                .toList();
    }

    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        return new OrderResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        order.updateStatus(status);
        return new OrderResponseDTO(order);
    }
}
