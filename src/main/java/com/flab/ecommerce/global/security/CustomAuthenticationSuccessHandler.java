package com.flab.ecommerce.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.ecommerce.domain.user.dto.LoginResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        LoginResponseDTO responseDTO = new LoginResponseDTO(200, "로그인 성공", "AUTHENTICATION_SUCCESS");
        response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
    }
}
