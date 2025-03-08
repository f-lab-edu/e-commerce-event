package com.flab.ecommerce.global.security.dto;

import lombok.Getter;

@Getter
public class LoginRequestDTO {
    private String email;
    private String password;
}
