package com.flab.ecommerce.domain.user.controller;

import com.flab.ecommerce.domain.user.dto.UserCreateRequestDTO;
import com.flab.ecommerce.domain.user.dto.UserResponseDTO;
import com.flab.ecommerce.domain.user.dto.UserUpdateRequestDTO;
import com.flab.ecommerce.domain.user.service.UserService;
import com.flab.ecommerce.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(@Valid @RequestBody UserCreateRequestDTO requestDTO) {
        UserResponseDTO responseDTO = userService.registerUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(responseDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> findMyInfo(Principal principal) {
        String email = principal.getName();
        UserResponseDTO user = userService.findMyInfo(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateMyInfo(@Valid @RequestBody UserUpdateRequestDTO requestDTO, Principal principal) {
        String email = principal.getName();
        UserResponseDTO user = userService.updateMyInfo(email, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping("/me/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateMyAccount(Principal principal) {
        String email = principal.getName();
        userService.deactivateUser(email);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료 되었습니다."));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> findAllUsers() {
        List<UserResponseDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> findUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateRequestDTO requestDTO) {
        UserResponseDTO user = userService.updateUserById(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactivateUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findUserById(id);
        userService.deactivateUserById(id);
        String message = String.format("사용자 %s(%s) 계정이 비활성화 되었습니다.", user.getName(), user.getEmail());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activateUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findUserById(id);
        userService.activateUserById(id);
        String message = String.format("사용자 %s(%s) 계정이 활성화 되었습니다.", user.getName(), user.getEmail());
        return ResponseEntity.ok(ApiResponse.success(message));
    }

}
