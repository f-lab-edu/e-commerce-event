package com.flab.ecommerce.domain.product.service;

import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.enums.ProductStatus;
import com.flab.ecommerce.domain.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductStatusChecker {

    private final ProductService productService;

    public ProductStatus check(Long id) {
        try {
            Product product = productService.getProduct(id);
            if (product.getStockQuantity() <= 0) {
                return ProductStatus.OUT_OF_STOCK;
            }
            return ProductStatus.AVAILABLE;
        } catch (ProductNotFoundException e) {
            return ProductStatus.DELETED;
        }
    }

    public int getStock(Long productId) {
        return productService.getProduct(productId).getStockQuantity();
    }
}
