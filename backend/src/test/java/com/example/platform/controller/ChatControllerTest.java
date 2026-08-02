package com.example.platform.controller;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.example.platform.service.MessageService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.platform.entity.User;
import com.example.platform.entity.Role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;
import com.example.platform.dto.websocket.ChatMessageRequest;
import com.example.platform.entity.MessageType;
import com.example.platform.entity.UserStatus;
import com.example.platform.exception.UserNotMemberOfChannelException;
import com.example.platform.security.CustomUserDetails;
import com.example.platform.dto.message.CreateMessageRequest;
import com.example.platform.dto.message.MessageResponse;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @InjectMocks
    private ChatController chatController;

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private Principal principal;

    private ChatMessageRequest request;
    private MessageResponse response;

    @Test
    void shouldSendMessageSuccessfully() {
        request = ChatMessageRequest.builder()
                .channelId(1L)
                .content("Hello")
                .type(MessageType.TEXT)
                .build();

        response = MessageResponse.builder()
                .id(1L)
                .content("Hello")
                .type(MessageType.TEXT)
                .senderId(1L)
                .senderUsername("roshan")
                .senderDisplayName("Roshan")
                .edited(false)
                .build();

        when(principal.getName()).thenReturn("roshan");
        when(messageService.sendMessage(request, "roshan"))
                .thenReturn(response);
        chatController.sendMessage(request, principal);
        verify(messageService).sendMessage(request, "roshan");
        verify(messagingTemplate).convertAndSend("/topic/channel/" + request.getChannelId(), response);
        verifyNoMoreInteractions(messageService, messagingTemplate);
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenPrincipalIsNull() {
        request = ChatMessageRequest.builder()
                .channelId(1L)
                .content("Hello")
                .type(MessageType.TEXT)
                .build();

        assertThrows(IllegalStateException.class, () -> chatController.sendMessage(request, null));
        verifyNoInteractions(messageService);
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void shouldPropagateServiceException() {
        request = ChatMessageRequest.builder()
                .channelId(1L)
                .content("Hello")
                .type(MessageType.TEXT)
                .build();

        when(principal.getName()).thenReturn("roshan");
        when(messageService.sendMessage(request, "roshan"))
                .thenThrow(new UserNotMemberOfChannelException("You don't have access to this channel."));

        assertThrows(
                UserNotMemberOfChannelException.class,
                () -> chatController.sendMessage(request, principal));

        verify(messageService).sendMessage(request, "roshan");
        verifyNoInteractions(messagingTemplate);
    }

}