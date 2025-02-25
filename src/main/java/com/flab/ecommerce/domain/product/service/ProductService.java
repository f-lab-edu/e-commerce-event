package com.flab.ecommerce.domain.product.service;

import com.flab.ecommerce.domain.product.dto.ProductCreateRequestDTO;
import com.flab.ecommerce.domain.product.dto.ProductDetailResponseDTO;
import com.flab.ecommerce.domain.product.dto.ProductListResponseDTO;
<<<<<<< HEAD
<<<<<<< HEAD
import com.flab.ecommerce.domain.product.dto.ProductUpdateRequestDTO;
=======
>>>>>>> 280c387 (Feat: 상품 등록 API 추가)
=======
import com.flab.ecommerce.domain.product.dto.ProductUpdateRequestDTO;
>>>>>>> 4d249de (Feat: 상품 수정 API 추가)
import com.flab.ecommerce.domain.product.entity.Category;
import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.repository.CategoryRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + id));
        return new ProductDetailResponseDTO(product);
    }

    @Transactional
    public ProductDetailResponseDTO createProduct(ProductCreateRequestDTO requestDTO) {
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

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
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 4d249de (Feat: 상품 수정 API 추가)

    @Transactional
    public ProductDetailResponseDTO updateProduct(long id, ProductUpdateRequestDTO requestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다.."));

        Category category = requestDTO.getCategoryId() == null
                ? product.getCategory()
                : categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        product.update(requestDTO, category);
        return new ProductDetailResponseDTO(product);
    }
<<<<<<< HEAD
=======
>>>>>>> 280c387 (Feat: 상품 등록 API 추가)
=======
>>>>>>> 4d249de (Feat: 상품 수정 API 추가)
}
