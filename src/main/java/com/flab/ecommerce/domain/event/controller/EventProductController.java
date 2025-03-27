package com.flab.ecommerce.domain.event.controller;

import com.flab.ecommerce.domain.event.dto.EventProductCreateRequestDTO;
import com.flab.ecommerce.domain.event.dto.EventProductResponseDTO;
import com.flab.ecommerce.domain.event.service.EventProductService;
import com.flab.ecommerce.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/products")
@RequiredArgsConstructor
@Tag(name = "이벤트 상품", description = "이벤트 상품 관리 API")
public class EventProductController {

    private final EventProductService eventProductService;

    @Operation(summary = "이벤트 상품 추가 (관리자 전용)", description = "이벤트에 상품을 추가합니다.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> addProductToEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid EventProductCreateRequestDTO requestDTO) {

        eventProductService.addProductToEvent(eventId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("이벤트 상품이 추가되었습니다."));
    }

    @Operation(summary = "이벤트 상품 목록 조회", description = "특정 이벤트의 상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventProductResponseDTO>>> getEventProducts(@PathVariable Long eventId) {
        List<EventProductResponseDTO> products = eventProductService.getProductsByEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}

