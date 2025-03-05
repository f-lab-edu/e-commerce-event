package com.flab.ecommerce.global.security.controller;

import com.flab.ecommerce.global.response.ApiResponse;
import com.flab.ecommerce.global.response.ErrorResponse;
import com.flab.ecommerce.global.security.dto.LoginRequestDTO;
import com.flab.ecommerce.global.security.dto.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name ="인증", description = "로그인 및 로그아웃 기능을 제공합니다.")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 합니다.")
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequestDTO request, HttpSession session) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            LoginResponseDTO response = new LoginResponseDTO(userDetails.getUsername());
            return ApiResponse.success(response);

        } catch (DisabledException e) {
            return ApiResponse.fail(401, "비활성화된 계정입니다.", "ACCOUNT_DISABLED");
        } catch (AuthenticationException e) {
            return ApiResponse.fail(401, "아이디 또는 비밀번호가 올바르지 않습니다.", "AUTHENTICATION_ERROR");
        }
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 처리합니다.")
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success("로그아웃 성공");
    }
}
