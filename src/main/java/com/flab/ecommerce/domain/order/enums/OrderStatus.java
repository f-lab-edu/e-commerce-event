package com.flab.ecommerce.domain.order.enums;

public enum OrderStatus {
    PENDING,   // 결제 대기 중
    PAID,      // 결제 완료
    SHIPPED,   // 배송 중
    DELIVERED, // 배송 완료
    CANCELED   // 주문 취소
}
