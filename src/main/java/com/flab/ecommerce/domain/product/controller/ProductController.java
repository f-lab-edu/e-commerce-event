package com.flab.ecommerce.domain.product.controller;


import com.flab.ecommerce.domain.product.dto.ProductDetailResponseDTO;
import com.flab.ecommerce.domain.product.dto.ProductListResponseDTO;
import com.flab.ecommerce.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductListResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDetailResponseDTO> getProductById(@PathVariable long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
