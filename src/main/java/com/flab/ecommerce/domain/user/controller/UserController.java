package com.flab.ecommerce.domain.user.controller;

import com.flab.ecommerce.domain.user.dto.UserCreateRequestDTO;
import com.flab.ecommerce.domain.user.dto.UserResponseDTO;
import com.flab.ecommerce.domain.user.dto.UserUpdateRequestDTO;
import com.flab.ecommerce.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> save(@Valid @RequestBody UserCreateRequestDTO requestDTO) {
        UserResponseDTO responseDTO = userService.save(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> findMyInfo(Principal principal) {
        String email = principal.getName();
        UserResponseDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyInfo(@Valid @RequestBody UserUpdateRequestDTO requestDTO, Principal principal) {
        String email = principal.getName();
        UserResponseDTO user = userService.updateUserByEmail(email, requestDTO);
        return ResponseEntity.ok(user);
    }

}
