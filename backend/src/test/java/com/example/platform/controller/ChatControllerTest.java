package com.example.platform.controller;

import static com.example.platform.entity.MessageType.TEXT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.platform.dto.message.CreateMessageRequest;
import com.example.platform.dto.message.MessageResponse;
import com.example.platform.dto.websocket.ChatMessageRequest;
import com.example.platform.exception.ChannelDoesNotExistException;
import com.example.platform.service.MessageService;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @Mock
    private Principal principal;

    @InjectMocks
    private ChatController chatController;

    private ChatMessageRequest request;
    private MessageResponse response;

    @BeforeEach
    void setUp() {

        request = ChatMessageRequest.builder()
                .channelId(1L)
                .content("Hello everyone!")
                .type(TEXT)
                .build();

        response = MessageResponse.builder()
                .id(10L)
                .content("Hello everyone!")
                .senderUsername("roshan")
                .build();

        when(headerAccessor.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn("roshan");
    }

    @Test
    void shouldSendMessageSuccessfully() {

        when(messageService.sendMessage(
                eq(1L),
                any(CreateMessageRequest.class),
                eq("roshan")))
                .thenReturn(response);

        chatController.sendMessage(request, headerAccessor);

        verify(messageService).sendMessage(
                eq(1L),
                any(CreateMessageRequest.class),
                eq("roshan"));

        verify(messagingTemplate)
                .convertAndSend("/topic/channel/1", response);
    }

    @Test
    void shouldPassCorrectRequestToMessageService() {

        when(messageService.sendMessage(
                eq(1L),
                any(CreateMessageRequest.class),
                eq("roshan")))
                .thenReturn(response);

        chatController.sendMessage(request, headerAccessor);

        verify(messageService).sendMessage(
                eq(1L),
                argThat(createRequest ->
                        createRequest.getContent().equals("Hello everyone!")
                                && createRequest.getType() == TEXT),
                eq("roshan"));
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsNull() {

        when(headerAccessor.getUser()).thenReturn(null);

        assertThrows(
                IllegalStateException.class,
                () -> chatController.sendMessage(request, headerAccessor));

        verifyNoInteractions(messageService);
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void shouldPropagateExceptionWhenServiceThrows() {

        when(messageService.sendMessage(
                eq(1L),
                any(CreateMessageRequest.class),
                eq("roshan")))
                .thenThrow(new ChannelDoesNotExistException("Channel not found"));

        assertThrows(
                ChannelDoesNotExistException.class,
                () -> chatController.sendMessage(request, headerAccessor));

        verify(messageService).sendMessage(
                eq(1L),
                any(CreateMessageRequest.class),
                eq("roshan"));

        verifyNoInteractions(messagingTemplate);
    }
}