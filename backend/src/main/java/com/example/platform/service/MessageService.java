package com.example.platform.service;

import com.example.platform.dto.message.CreateMessageRequest;
import com.example.platform.dto.message.MessageResponse;
import com.example.platform.dto.websocket.ChatMessageRequest;
import com.example.platform.security.CustomUserDetails;

import java.util.List;

public interface MessageService {
    MessageResponse sendMessage(Long channelId, CreateMessageRequest request, CustomUserDetails currentUser);
    List<MessageResponse> getMessages(Long channelId, CustomUserDetails currentUser);
    MessageResponse sendMessage(ChatMessageRequest request,String username);
} 