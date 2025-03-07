package com.flab.ecommerce.domain.product.controller;


import com.flab.ecommerce.domain.product.dto.ProductCreateRequestDTO;
import com.flab.ecommerce.domain.product.dto.ProductDetailResponseDTO;
import com.flab.ecommerce.domain.product.dto.ProductListResponseDTO;
import com.flab.ecommerce.domain.product.dto.ProductUpdateRequestDTO;
import com.flab.ecommerce.domain.product.service.ProductService;
import com.flab.ecommerce.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품", description = "상품 정보 조회 및 관리 기능을 제공합니다.")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 전체 조회", description = "모든 상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductListResponseDTO>>> getAllProducts() {
        List<ProductListResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상품 상세 정보를 조회합니다.")
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> getProductById(@PathVariable long id) {
        ProductDetailResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @Operation(summary = "카테고리별 상품 조회", description = "특정 카테고리에 속한 상품 목록을 조회합니다.")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductListResponseDTO>>> getProductsByCategory(@PathVariable long categoryId) {
        List<ProductListResponseDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "상품 등록 (관리자 전용)", description = "관리자가 새로운 상품을 등록합니다.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> createProduct(@RequestBody @Valid ProductCreateRequestDTO requestDTO) {
        ProductDetailResponseDTO product = productService.createProduct(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @Operation(summary = "상품 수정 (관리자 전용)", description = "관리자가 기존 상품 정보를 수정합니다.")
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> updateProduct(@PathVariable long id, @RequestBody @Valid ProductUpdateRequestDTO requestDTO) {
        ProductDetailResponseDTO product = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @Operation(summary = "상품 삭제 (관리자 전용)", description = "관리자가 상품을 삭제합니다.")
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("상품 삭제가 완료 되었습니다."));
    }

}
