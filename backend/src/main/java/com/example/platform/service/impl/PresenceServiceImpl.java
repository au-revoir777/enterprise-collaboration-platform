package com.example.platform.service.impl;

import com.example.platform.controller.AuthController;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.example.platform.service.PresenceService;
import com.example.platform.entity.UserStatus;
import com.example.platform.exception.UserNotFoundException;
import com.example.platform.repository.UserRepository;
import com.example.platform.dto.PresenceResponse;
import com.example.platform.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {
    private final UserRepository userRepository;


    private PresenceResponse mapToPresenceResponse(User user){
        return PresenceResponse.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .status(user.getStatus())
        .build();
    }

    @Override
    public PresenceResponse setUserOnline(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("Username not found."));

        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
        return mapToPresenceResponse(user);
    }

    @Override
    public PresenceResponse setUserOffline(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("Username not found."));

        user.setStatus(UserStatus.OFFLINE);
        userRepository.save(user);
        return mapToPresenceResponse(user);
    }

    @Override
    public List<PresenceResponse> getAllPresences() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToPresenceResponse)
                .toList();
    }


}
