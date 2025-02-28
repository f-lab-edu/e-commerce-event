package com.flab.ecommerce.domain.product.controller;


import com.flab.ecommerce.domain.product.dto.ProductCreateRequestDTO;
import com.flab.ecommerce.domain.product.dto.ProductDetailResponseDTO;
import com.flab.ecommerce.domain.product.dto.ProductListResponseDTO;
import com.flab.ecommerce.domain.product.dto.ProductUpdateRequestDTO;
import com.flab.ecommerce.domain.product.service.ProductService;
import com.flab.ecommerce.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductListResponseDTO>>> getAllProducts() {
        List<ProductListResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> getProductById(@PathVariable long id) {
        ProductDetailResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> createProduct(@RequestBody ProductCreateRequestDTO requestDTO) {
        ProductDetailResponseDTO product = productService.createProduct(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @PatchMapping("{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> updateProduct(@PathVariable long id, @RequestBody ProductUpdateRequestDTO requestDTO) {
        ProductDetailResponseDTO product = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

}
