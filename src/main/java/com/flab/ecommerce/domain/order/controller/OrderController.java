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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "주문", description = "주문 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final LoginMemberProvider loginMemberProvider;

    @Operation(summary = "내 주문 목록 조회", description = "현재 로그인한 사용자의 주문 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getMyOrders(Principal principal) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        return ResponseEntity.ok(ApiResponse.success(orderService.getMyOrders(memberId)));
    }


    @Operation(summary = "주문 생성", description = "장바구니에 담긴 상품을 주문합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeOrder(Principal principal, @Valid @RequestBody(required = false) OrderRequestDTO request) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        OrderResponseDTO order = orderService.placeOrder(memberId, request);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @Operation(summary = "주문 취소", description = "해당 주문을 취소합니다. 본인의 주문만 취소할 수 있습니다.")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(Principal principal, @PathVariable Long orderId) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        orderService.cancelOrder(orderId, memberId);
        return ResponseEntity.ok(ApiResponse.success("주문이 취소되었습니다."));
    }

    @Operation(summary = "모든 주문 조회 (관리자)", description = "관리자가 전체 주문 목록을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @Operation(summary = "특정 주문 조회 (관리자)", description = "관리자가 특정 주문을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(@PathVariable Long orderId) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @Operation(summary = "주문 삭제 (관리자)", description = "관리자가 특정 주문을 삭제합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("주문이 삭제되었습니다."));
    }


}
