package com.flab.ecommerce.domain.product.service;

import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductStatusChecker {

    private final ProductService productService;

    public ProductStatus check(Long id) {
        Product product = productService.findProductOrNull(id);
        if (product == null) {
            return ProductStatus.DELETED;
        }
        if (product.getStockQuantity() <= 0) {
            return ProductStatus.OUT_OF_STOCK;
        }
        return ProductStatus.AVAILABLE;
    }

    public int getStock(Long productId) {
        Product product = productService.findProductOrNull(productId);
        return (product == null) ? 0 : product.getStockQuantity();
    }
}
