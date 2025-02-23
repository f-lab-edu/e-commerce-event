package com.flab.ecommerce.domain.product.repository;

import com.flab.ecommerce.domain.product.entity.Category;
import com.flab.ecommerce.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);

}
