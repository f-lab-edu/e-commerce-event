package com.flab.ecommerce.domain.member.dto;

import com.flab.ecommerce.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberResponseDTO {
    private String email;
    private String name;
    private String role;

    public MemberResponseDTO(Member user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole().name();
    }
}
