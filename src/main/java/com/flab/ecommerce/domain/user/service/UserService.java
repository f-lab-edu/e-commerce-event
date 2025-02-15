package com.flab.ecommerce.domain.user.service;

import com.flab.ecommerce.domain.user.dto.UserCreateRequestDTO;
import com.flab.ecommerce.domain.user.dto.UserResponseDTO;
import com.flab.ecommerce.domain.user.dto.UserUpdateRequestDTO;
import com.flab.ecommerce.domain.user.entity.UserEntity;
import com.flab.ecommerce.domain.user.enums.UserRole;
import com.flab.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO save(UserCreateRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        UserEntity user = UserEntity.builder()
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .password(encodedPassword)
                .role(UserRole.USER)
                .build();
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        return new UserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUserByEmail(String email, UserUpdateRequestDTO requestDTO) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        boolean isChanged = false;

        if (requestDTO.getName() != null
                && !requestDTO.getName().isEmpty()
                && !requestDTO.getName().equals(user.getName())) {
            user.setName(requestDTO.getName());
            isChanged = true;
        }

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
                String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
                user.setPassword(encodedPassword);
                isChanged = true;
            }
        }

        if (isChanged) {
            userRepository.save(user);
        }

        return new UserResponseDTO(user);
    }
}

