package com.example.platform.dto;

import com.example.platform.entity.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.platform.entity.Role;


// this is what the backend sends to the user
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String displayName;
    private String email;
    private Role role;
    private UserStatus status;
    private String profilePictureUrl;
}
