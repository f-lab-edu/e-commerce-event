package com.flab.ecommerce.global.security.dto;

import lombok.Getter;



@Getter
public class LoginResponseDTO {
    private final String email;

    public LoginResponseDTO(String email) {
        this.email = email;
    }
}

