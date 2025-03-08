package com.flab.ecommerce.domain.order.controller;

import com.flab.ecommerce.domain.order.dto.OrderRequestDTO;
import com.flab.ecommerce.domain.order.dto.OrderResponseDTO;
import com.flab.ecommerce.domain.order.service.OrderService;
import com.flab.ecommerce.global.response.ApiResponse;
import com.flab.ecommerce.global.security.provider.LoginMemberProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "주문", description = "주문 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final LoginMemberProvider loginMemberProvider;

    @Operation(summary = "주문 생성", description = "장바구니에 담긴 상품을 주문합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeOrder(Principal principal, @RequestBody(required = false) @Valid OrderRequestDTO request) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        OrderResponseDTO order = orderService.placeOrder(memberId, request);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
}
