package com.flab.ecommerce.domain.member.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequestDTO {

    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력하세요.")
    private String name;

    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 입력하세요.")
    private String password;
}
