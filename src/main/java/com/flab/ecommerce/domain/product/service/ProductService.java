package com.flab.ecommerce.domain.product.service;

import com.flab.ecommerce.domain.product.dto.ProductCreateRequestDTO;
import com.flab.ecommerce.domain.product.dto.ProductDetailResponseDTO;
import com.flab.ecommerce.domain.product.dto.ProductListResponseDTO;
import com.flab.ecommerce.domain.product.dto.ProductUpdateRequestDTO;
import com.flab.ecommerce.domain.category.entity.Category;
import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.category.exception.CategoryNotFoundException;
import com.flab.ecommerce.domain.product.exception.ProductNotFoundException;
import com.flab.ecommerce.domain.category.repository.CategoryRepository;
import com.flab.ecommerce.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ProductNotFoundException(id));
        return new ProductDetailResponseDTO(product);
    }

    @Transactional
    public ProductDetailResponseDTO createProduct(ProductCreateRequestDTO requestDTO) {
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        Product product = Product.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .price(requestDTO.getPrice())
                .stockQuantity(requestDTO.getStockQuantity())
                .imageUrl(requestDTO.getImageUrl())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        return new ProductDetailResponseDTO(savedProduct);
    }

    @Transactional
    public ProductDetailResponseDTO updateProduct(long id, ProductUpdateRequestDTO requestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = requestDTO.getCategoryId() == null
                ? product.getCategory()
                : categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        product.update(requestDTO, category);
        return new ProductDetailResponseDTO(product);
    }

    @Transactional
    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.delete(product);
    }
}
