package com.example.platform.service.impl;

import com.example.platform.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.platform.dto.RegisterRequest;
import com.example.platform.dto.UserResponse;
import com.example.platform.entity.UserStatus;
import com.example.platform.entity.User;
import com.example.platform.entity.Role;
import com.example.platform.exception.EmailAlreadyExistsException;
import com.example.platform.exception.InvalidCredentialsException;
import com.example.platform.exception.PasswordMismatchException;
import com.example.platform.exception.UsernameAlreadyExistsException;
import com.example.platform.repository.UserRepository;
import com.example.platform.security.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.platform.dto.LoginRequest;
import com.example.platform.dto.LoginResponse;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private UserResponse mapToUserResponse(User user) {
    return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .displayName(user.getDisplayName())
            .email(user.getEmail())
            .role(user.getRole())
            .status(user.getStatus())
            .build();
    }

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
        
        UserResponse response = mapToUserResponse(savedUser);
        return response;
    }

    @Override
    public UserResponse getUserById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public LoginResponse login(LoginRequest request){
        String identifier = request.getIdentifier();
        User user;
        Optional<User> optionalUser;

        if (identifier.contains("@")) {
            optionalUser = userRepository.findByEmail(identifier);
        } else {
            optionalUser = userRepository.findByUsername(identifier);
        }

        user = optionalUser.orElseThrow(() ->
                new InvalidCredentialsException(
                    "Invalid username/email or password."
                ));

        if(passwordEncoder.matches(request.getPassword(),user.getPassword())){

            String token = jwtService.generateToken(user);
            
            UserResponse response = mapToUserResponse(user);

            return LoginResponse.builder()
            .token(token)
            .user(response)
            .build();

        } else {
            throw new InvalidCredentialsException("Invalid username/email or password.");
        }
    }
}
