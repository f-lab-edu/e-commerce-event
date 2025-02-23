package com.flab.ecommerce.domain.product.dto;

<<<<<<< HEAD
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
=======
>>>>>>> 280c387 (Feat: 상품 등록 API 추가)
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
<<<<<<< HEAD
@AllArgsConstructor
@Builder
public class ProductCreateRequestDTO {
    @NotBlank(message = "상품명을 입력해주세요.")
    private String name;

    @NotBlank(message = "상품 설명을 입력해주세요.")
    private String description;

    @NotNull(message = "상품 가격을 입력해주세요.")
    @Min(value = 1, message = "상품 가격은 1원 이상이어야 합니다.")
    private BigDecimal price;

    @NotNull(message = "상품 재고를 입력해주세요.")
    @Min(value = 0, message = "상품 재고는 0개 이상이어야 합니다.")
    private int stockQuantity;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    private String imageUrl;

=======
public class ProductCreateRequestDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private String imageUrl;
    private Long categoryId;

>>>>>>> 280c387 (Feat: 상품 등록 API 추가)
}
