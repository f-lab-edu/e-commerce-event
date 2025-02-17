package com.flab.ecommerce.domain.user.dto;

import com.flab.ecommerce.domain.user.entity.UserEntity;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private String email;
    private String name;
    private String role;

    public UserResponseDTO(UserEntity user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole().name();
    }
}
