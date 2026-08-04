package com.example.platform.dto;

import com.example.platform.entity.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresenceResponse {
    private Long userId;
    private String username;
    private UserStatus status;
}
