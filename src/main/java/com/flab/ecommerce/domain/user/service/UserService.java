package com.flab.ecommerce.domain.user.service;

import com.flab.ecommerce.domain.user.dto.UserCreateRequestDTO;
import com.flab.ecommerce.domain.user.dto.UserCreateResponseDTO;
import com.flab.ecommerce.domain.user.entity.User;
import com.flab.ecommerce.domain.user.enums.UserRole;
import com.flab.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreateResponseDTO createUser(UserCreateRequestDTO requestDTO) {
        if(userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        User user = User.builder()
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .password(encodedPassword)
                .role(UserRole.ROLE_USER)
                .build();
        userRepository.save(user);
        return new UserCreateResponseDTO(user);
    }


}
