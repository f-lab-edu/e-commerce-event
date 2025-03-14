package com.flab.ecommerce.domain.cart.controller;

import com.flab.ecommerce.domain.cart.dto.CartItemAddRequestDTO;
import com.flab.ecommerce.domain.cart.dto.CartItemUpdateRequestDTO;
import com.flab.ecommerce.domain.cart.dto.CartResponseDTO;
import com.flab.ecommerce.domain.cart.service.CartService;
import com.flab.ecommerce.domain.member.service.MemberService;
import com.flab.ecommerce.global.response.ApiResponse;
import com.flab.ecommerce.global.security.provider.LoginMemberProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "장바구니", description = "장바구니 관리 기능을 제공합니다.")
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final LoginMemberProvider loginMemberProvider;

    @Operation(
            summary = "장바구니 조회",
            description = "현재 로그인한 회원의 장바구니 내역을 조회합니다.\n" +
                    "재고가 부족하거나, 삭제된 상품도 함께 표시되며 해당 상태도 확인할 수 있습니다."
    )
    @GetMapping()
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart(Principal principal) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        CartResponseDTO cart = cartService.getCart(memberId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @Operation(
            summary = "장바구니 상품 추가",
            description = "선택한 상품을 장바구니에 추가합니다.\n" +
                    "이미 장바구니에 담긴 상품이라면 수량이 증가하고, 새로운 상품이라면 새로 추가됩니다."
    )
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addCartItem(
            Principal principal,
            @RequestBody @Valid CartItemAddRequestDTO requestDTO) {

        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        CartResponseDTO cart = cartService.addCartItem(memberId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @Operation(
            summary = "장바구니 상품 수량 변경",
            description = "장바구니에 담긴 특정 상품의 수량을 변경합니다.\n" +
                    "재고 체크 후 변경되며, 재고보다 더 큰 수량으로는 변경할 수 없습니다."
    )
    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItemQuantity(
            Principal principal,
            @PathVariable Long productId,
            @RequestBody @Valid CartItemUpdateRequestDTO request) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        return ResponseEntity.ok(ApiResponse.success(cartService.updateCartItemQuantity(memberId, productId, request.getQuantity())));
    }

    @Operation(
            summary = "장바구니 상품 삭제",
            description = "장바구니에 담긴 특정 상품을 삭제합니다.\n" +
                    "상품이 삭제되면 장바구니에서 즉시 제거됩니다."
    )
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> removeCartItem(Principal principal, @PathVariable Long productId) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        return ResponseEntity.ok(ApiResponse.success(cartService.removeCartItem(memberId, productId)));
    }

    @DeleteMapping()
    @Operation(summary = "장바구니 전체 비우기", description = "장바구니에 담긴 모든 상품을 삭제합니다.")
    public ResponseEntity<ApiResponse<CartResponseDTO>> clearCart(Principal principal) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        return ResponseEntity.ok(ApiResponse.success(cartService.clearCart(memberId)));
    }



}
