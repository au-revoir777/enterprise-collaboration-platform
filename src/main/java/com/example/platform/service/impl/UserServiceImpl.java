package com.example.platform.service.impl;

import com.example.platform.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.platform.dto.RegisterRequest;
import com.example.platform.dto.UserResponse;
import com.example.platform.entity.UserStatus;
import com.example.platform.entity.User;
import com.example.platform.entity.Role;
import com.example.platform.exception.EmailAlreadyExistsException;
import com.example.platform.exception.PasswordMismatchException;
import com.example.platform.exception.UsernameAlreadyExistsException;
import com.example.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email is already registered.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new PasswordMismatchException("Passwords do not match.");
        }

        User user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .displayName(request.getDisplayName())
        .profilePictureUrl(request.getProfilePictureUrl())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .status(UserStatus.OFFLINE)
        .build();

        User savedUser = userRepository.save(user);
        
        return UserResponse.builder()
        .id(savedUser.getId())
        .username(savedUser.getUsername())
        .displayName(savedUser.getDisplayName())
        .email(savedUser.getEmail())
        .role(savedUser.getRole())
        .status(savedUser.getStatus())
        .profilePictureUrl(savedUser.getProfilePictureUrl())
        .build();
    }

    @Override
    public UserResponse getUserById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
