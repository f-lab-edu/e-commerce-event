package com.flab.ecommerce.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.ecommerce.global.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String message = "아이디 또는 비밀번호가 올바르지 않습니다.";
        if (exception instanceof DisabledException) {
            message = "비활성화된 계정입니다. 관리자에게 문의하세요.";
        }

        ErrorResponse errorResponse = new ErrorResponse(401, message, "AUTHENTICATION_ERROR");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
