package com.flab.ecommerce.domain.member.dto;

import com.flab.ecommerce.domain.common.Address;
import com.flab.ecommerce.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberResponseDTO {
    private String email;
    private String name;
    private String role;
    private String phoneNumber;
    private Address address;

    public MemberResponseDTO(Member member) {
        this.email = member.getEmail();
        this.name = member.getName();
        this.role = member.getRole().name();
        this.phoneNumber = member.getPhoneNumber();
        this.address = member.getAddress();
    }
}
