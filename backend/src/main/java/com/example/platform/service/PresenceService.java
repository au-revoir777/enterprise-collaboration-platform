package com.example.platform.service;

import com.example.platform.dto.PresenceResponse;

import java.util.List;

public interface PresenceService {
    PresenceResponse setUserOnline(String username);
    PresenceResponse setUserOffline(String username);
    List<PresenceResponse> getAllPresences();
}
