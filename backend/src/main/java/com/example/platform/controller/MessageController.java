package com.example.platform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.platform.dto.message.CreateMessageRequest;
import com.example.platform.dto.message.MessageResponse;
import com.example.platform.security.CustomUserDetails;

import com.example.platform.service.MessageService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/channels")
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/{channelId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long channelId,
            @Valid @RequestBody CreateMessageRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        MessageResponse response = messageService.sendMessage(channelId, request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
