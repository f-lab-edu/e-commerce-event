package com.flab.ecommerce.domain.order.dto;

import com.flab.ecommerce.domain.common.ShippingAddress;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequestDTO {
    private ShippingAddress shippingAddress;
}
