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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO registerUser(UserCreateRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        UserEntity user = UserEntity.builder()
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .password(encodedPassword)
                .role(UserRole.USER)
                .isActive(true)
                .build();
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findMyInfo(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        return new UserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateMyInfo(String email, UserUpdateRequestDTO requestDTO) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (requestDTO.getName() != null && !requestDTO.getName().isEmpty()
                && !requestDTO.getName().equals(user.getName())) {
            user.setName(requestDTO.getName());
        }

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
                String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
                user.setPassword(encodedPassword);
            }
        }

        return new UserResponseDTO(user);
    }


    @Transactional
    public void deactivateUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        user.deactivate();
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAllUsers(String adminEmail) {
        UserEntity admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("관리자만 유저 목록을 조회할 수 있습니다.");
        }
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(Long id, String adminEmail) {
        UserEntity admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("관리자만 유저를 조회할 수 있습니다.");
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        return new UserResponseDTO(user);
    }
}

