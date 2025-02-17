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
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserCreateRequestDTO requestDTO) {
        UserResponseDTO responseDTO = userService.registerUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> findMyInfo(Principal principal) {
        String email = principal.getName();
        UserResponseDTO user = userService.findMyInfo(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyInfo(@Valid @RequestBody UserUpdateRequestDTO requestDTO, Principal principal) {
        String email = principal.getName();
        UserResponseDTO user = userService.updateMyInfo(email, requestDTO);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(Principal principal) {
        String email = principal.getName();
        userService.deactivateUser(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAllUsers() {
        List<UserResponseDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }




}
