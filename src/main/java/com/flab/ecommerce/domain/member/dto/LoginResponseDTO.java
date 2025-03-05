package com.flab.ecommerce.domain.member.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LoginResponseDTO {
    private final int status;
    private final String message;
    private final String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public LoginResponseDTO(int status, String message, String email) {
        this.status = status;
        this.message = message;
        this.email = email;
        this.timestamp = LocalDateTime.now();
    }
}

