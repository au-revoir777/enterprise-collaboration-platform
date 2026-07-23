package com.example.platform.service;

import com.example.platform.dto.RegisterRequest;
import com.example.platform.dto.UserResponse;
import com.example.platform.dto.LoginRequest;
import com.example.platform.dto.LoginResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
    LoginResponse login(LoginRequest request);
}
