package com.flab.ecommerce.domain.user.dto;

import com.flab.ecommerce.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private String email;
    private String name;
    private String role;

    public UserResponseDTO(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole().name();
    }
}
