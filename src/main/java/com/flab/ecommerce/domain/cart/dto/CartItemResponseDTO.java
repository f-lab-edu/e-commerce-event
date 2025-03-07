package com.flab.ecommerce.domain.cart.dto;

import com.flab.ecommerce.domain.cart.entity.CartItem;
import com.flab.ecommerce.domain.product.entity.Product;
import lombok.Getter;

@Getter
public class CartItemResponseDTO {
    private Long productId;
    private String productName;
    private int quantity;           // 장바구니에 담긴 수량
    private int realStock;          // 실시간 재고
    private int shortageCount;      // 부족 수량 (0이면 정상)
    private boolean outOfStock;     // 품절 여부
    private boolean purchasable;    // 구매 가능 여부
    private int maxPurchasableCount; // 최대 구매 가능 수량

    public CartItemResponseDTO(CartItem item, Product product) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.quantity = item.getQuantity();
        this.realStock = product.getStockQuantity();

        int shortage = quantity - realStock;
        this.shortageCount = Math.max(0, shortage);

        this.outOfStock = (realStock == 0);
        this.purchasable = (shortage <= 0); // 부족하지 않으면 구매 가능
        this.maxPurchasableCount = Math.min(quantity, realStock); // 최대 구매 가능 수량
    }

}
