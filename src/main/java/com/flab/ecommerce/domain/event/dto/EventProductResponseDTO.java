package com.flab.ecommerce.domain.event.dto;

import com.flab.ecommerce.domain.event.entity.EventProduct;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class EventProductResponseDTO {
    private final String name;
    private final BigDecimal price;
    private final String description;

    public EventProductResponseDTO(EventProduct product) {
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
    }
}
