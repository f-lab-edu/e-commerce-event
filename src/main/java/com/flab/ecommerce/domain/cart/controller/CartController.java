package com.flab.ecommerce.domain.cart.controller;

import com.flab.ecommerce.domain.cart.dto.CartItemAddRequestDTO;
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
    private final MemberService memberService;
    private final LoginMemberProvider loginMemberProvider;

    @Operation(summary = "내 장바구니 조회", description = "현재 로그인한 회원의 장바구니를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getMyCart(Principal principal) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        CartResponseDTO cart = cartService.getCart(memberId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @Operation(summary = "내 장바구니에 상품 추가", description = "현재 로그인한 회원의 장바구니에 상품을 추가합니다.")
    @PostMapping("/me/items")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addItemToMyCart(
            Principal principal,
            @RequestBody @Valid CartItemAddRequestDTO requestDTO) {

        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        CartResponseDTO cart = cartService.addItem(memberId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }


}
