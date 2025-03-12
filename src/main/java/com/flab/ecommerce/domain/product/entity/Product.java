package com.flab.ecommerce.domain.product.entity;

import com.flab.ecommerce.domain.category.entity.Category;
import com.flab.ecommerce.domain.product.dto.ProductUpdateRequestDTO;
import com.flab.ecommerce.domain.product.enums.ProductStatus;
import com.flab.ecommerce.domain.product.exception.OutOfStockException;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.AVAILABLE;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void update(ProductUpdateRequestDTO requestDTO, Category newCategory) {
        this.name = requestDTO.getName();
        this.description = requestDTO.getDescription();
        this.price = requestDTO.getPrice();
        this.stockQuantity = requestDTO.getStockQuantity();
        this.status = requestDTO.getStatus();
        this.imageUrl = requestDTO.getImageUrl();
        this.category = newCategory;
    }

    public void checkStock(int requestQuantity) {
        if (this.stockQuantity <= 0) {
            throw new OutOfStockException(this.id, this.stockQuantity, requestQuantity);
        }

        if(this.stockQuantity < requestQuantity) {
            throw new OutOfStockException(this.id, this.stockQuantity, requestQuantity);
        }
    }

    public void decreaseStock(int quantity) {
        if(this.stockQuantity < quantity) {
            throw new OutOfStockException(this.id, this.stockQuantity, quantity);
        }
        this.stockQuantity -= quantity;
    }

    public void delete() {
        this.status = ProductStatus.DELETED;
    }
}
