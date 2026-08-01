package com.example.platform.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.platform.dto.message.CreateMessageRequest;
import com.example.platform.dto.message.MessageResponse;
import com.example.platform.dto.websocket.ChatMessageRequest;
import com.example.platform.service.MessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(
        ChatMessageRequest request,
        SimpMessageHeaderAccessor headerAccessor) {

    System.out.println("=== ChatController reached ===");
    Principal principal = headerAccessor.getUser();

    System.out.println("Principal = " + principal);

    if (principal == null) {
        throw new IllegalStateException("Principal is null");
    }

    CreateMessageRequest createRequest =
            CreateMessageRequest.builder()
                    .content(request.getContent())
                    .type(request.getType())
                    .build();

    MessageResponse response =
            messageService.sendMessage(
                    request.getChannelId(),
                    createRequest,
                    principal.getName());

    messagingTemplate.convertAndSend(
            "/topic/channel/" + request.getChannelId(),
            response);
}
}