package com.flab.ecommerce.domain.user.dto;

import com.flab.ecommerce.domain.user.entity.User;
import com.flab.ecommerce.domain.user.enums.UserRole;
import lombok.Getter;

@Getter
public class UserCreateResponseDTO {
    private String email;
    private String name;
    private String role;

    public UserCreateResponseDTO(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole().name();
    }
}
