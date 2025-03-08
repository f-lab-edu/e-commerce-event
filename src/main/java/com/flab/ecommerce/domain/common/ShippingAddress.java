package com.flab.ecommerce.domain.common;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {

    @NotBlank(message = "수령인 이름은 필수 입력값입니다.")
    private String receiverName;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    private String phoneNumber;

    @NotBlank(message = "우편번호는 필수 입력값입니다.")
    private String zipCode;

    @NotBlank(message = "기본 주소는 필수 입력값입니다.")
    private String baseAddress;

    @NotBlank(message = "상세 주소는 필수 입력값입니다.")
    private String detailAddress;
}