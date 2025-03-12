package com.flab.ecommerce.domain.product.repository;

import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(long categoryId);

    List<Product> findByStatusNot(ProductStatus status);
}
