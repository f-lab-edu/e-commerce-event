package com.flab.ecommerce.domain.common;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    private String receiverName;
    private String phoneNumber;
    private String zipCode;
    private String baseAddress;
    private String detailAddress;
}