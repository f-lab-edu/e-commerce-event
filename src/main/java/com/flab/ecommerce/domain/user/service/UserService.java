package com.flab.ecommerce.domain.user.service;

import com.flab.ecommerce.domain.user.dto.UserCreateRequestDTO;
import com.flab.ecommerce.domain.user.dto.UserResponseDTO;
import com.flab.ecommerce.domain.user.dto.UserUpdateRequestDTO;
import com.flab.ecommerce.domain.user.entity.User;
import com.flab.ecommerce.domain.user.enums.UserRole;
import com.flab.ecommerce.domain.user.exception.EmailAlreadyExistsException;
import com.flab.ecommerce.domain.user.exception.UserNotFoundException;
import com.flab.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO registerUser(UserCreateRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        User user = User.builder()
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .password(encodedPassword)
                .role(UserRole.USER)
                .isActive(true)
                .build();
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    public UserResponseDTO findMyInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        return new UserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateMyInfo(String email, UserUpdateRequestDTO requestDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (requestDTO.getName() != null && !requestDTO.getName().isEmpty()
                && !requestDTO.getName().equals(user.getName())) {
            user.setName(requestDTO.getName());
        }

        if (requestDTO.getPassword() != null
                && !requestDTO.getPassword().isEmpty()
                && !passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {

                String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
                user.setPassword(encodedPassword);
            }

        return new UserResponseDTO(user);
    }


    @Transactional
    public void deactivateUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        user.deactivate();
    }

    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return new UserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUserById(Long id, UserUpdateRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (requestDTO.getName() != null && !requestDTO.getName().isEmpty()
                && !requestDTO.getName().equals(user.getName())) {
            user.setName(requestDTO.getName());
        }

        if (requestDTO.getPassword() != null
                && !requestDTO.getPassword().isEmpty()
                && !passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {

            String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());
            user.setPassword(encodedPassword);
        }

        return new UserResponseDTO(user);
    }

    @Transactional
    public void deactivateUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        user.deactivate();
    }

    @Transactional
    public void activateUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        user.activate();
    }

}

