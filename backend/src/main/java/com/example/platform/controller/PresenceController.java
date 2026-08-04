package com.example.platform.controller;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.platform.dto.PresenceResponse;
import com.example.platform.service.PresenceService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/presences")
    public List<PresenceResponse> getAllPresences() {
        return presenceService.getAllPresences();
    }
}
