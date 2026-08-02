package com.example.platform.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.platform.dto.message.MessageResponse;
import com.example.platform.dto.websocket.ChatMessageRequest;
import com.example.platform.service.MessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String CHANNEL_TOPIC = "/topic/channel/";

    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload ChatMessageRequest request,
            Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("Principal is null");
        }

        String username = principal.getName();
        MessageResponse response = messageService.sendMessage(request, username);

        messagingTemplate.convertAndSend(
                 CHANNEL_TOPIC + request.getChannelId(),
                 response);
    }
}