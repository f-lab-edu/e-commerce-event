package com.flab.ecommerce.domain.user.controller;

import com.flab.ecommerce.domain.user.dto.UserCreateRequestDTO;
import com.flab.ecommerce.domain.user.dto.UserCreateResponseDTO;
import com.flab.ecommerce.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserCreateResponseDTO> createUser(@Valid @RequestBody UserCreateRequestDTO requestDTO) {
        UserCreateResponseDTO responseDTO = userService.createUser(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
