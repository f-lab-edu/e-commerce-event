package com.flab.ecommerce.domain.product.service;

import com.flab.ecommerce.domain.product.dto.ProductDetailResponseDTO;
import com.flab.ecommerce.domain.product.dto.ProductListResponseDTO;
import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.repository.CategoryRepository;
import com.flab.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductListResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductListResponseDTO::new)
                .toList();
    }

    public ProductDetailResponseDTO getProductById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id));
        return new ProductDetailResponseDTO(product);
    }
}
