package com.example.platform.service;

import com.example.platform.dto.message.CreateMessageRequest;
import com.example.platform.dto.message.MessageResponse;
import com.example.platform.security.CustomUserDetails;

public interface MessageService {
    MessageResponse sendMessage( Long channelId, CreateMessageRequest request, CustomUserDetails currentUser);
} 