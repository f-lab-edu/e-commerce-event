package com.flab.ecommerce.domain.product.entity;

import com.flab.ecommerce.domain.category.entity.Category;
import com.flab.ecommerce.domain.product.dto.ProductUpdateRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private int stockQuantity;
    private String imageUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void update(ProductUpdateRequestDTO requestDTO, Category newCategory) {
        if (requestDTO.getName() != null && !requestDTO.getName().isEmpty()) {
            this.name = requestDTO.getName();
        }
        if (requestDTO.getDescription() != null && !requestDTO.getDescription().isEmpty()) {
            this.description = requestDTO.getDescription();
        }
        if (requestDTO.getPrice() != null) {
            this.price = requestDTO.getPrice();
        }
        if (requestDTO.getStockQuantity() > 0) {
            this.stockQuantity = requestDTO.getStockQuantity();
        }
        if (requestDTO.getImageUrl() != null && !requestDTO.getImageUrl().isEmpty()) {
            this.imageUrl = requestDTO.getImageUrl();
        }
        if (newCategory != null) {
            this.category = newCategory;
        }
    }

}
