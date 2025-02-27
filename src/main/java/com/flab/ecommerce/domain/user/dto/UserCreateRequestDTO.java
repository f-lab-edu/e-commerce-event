package com.flab.ecommerce.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateRequestDTO {

    @Email(message = "유효한 이메일을 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력값 입니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력값 입니다.")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력하세요.")
    private String name;

    @NotBlank(message = "비밀번호는 필수 입력값 입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 입력하세요.")
    private String password;

    public UserCreateRequestDTO(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
