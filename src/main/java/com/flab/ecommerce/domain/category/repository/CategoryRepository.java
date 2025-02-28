package com.flab.ecommerce.domain.category.repository;

import com.flab.ecommerce.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentCategory(Category parentCategory);
}
