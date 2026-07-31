package com.example.platform.service.impl;

import com.example.platform.dto.message.CreateMessageRequest;
import com.example.platform.dto.message.MessageResponse;
import com.example.platform.entity.*;
import com.example.platform.exception.ChannelDoesNotExistException;
import com.example.platform.exception.UserNotMemberOfChannelException;
import com.example.platform.repository.ChannelMemberRepository;
import com.example.platform.repository.ChannelRepository;
import com.example.platform.repository.MessageRepository;
import com.example.platform.security.CustomUserDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ChannelMemberRepository channelMemberRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private User user;
    private Channel channel;
    private CustomUserDetails currentUser;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .username("roshan")
                .displayName("Roshan")
                .email("roshan@example.com")
                .password("password")
                .role(Role.USER)
                .status(UserStatus.ONLINE)
                .active(true)
                .build();

        channel = Channel.builder()
                .id(10L)
                .name("general")
                .type(ChannelType.PUBLIC)
                .build();

        currentUser = new CustomUserDetails(user);
    }

    @Test
    void shouldSendMessageSuccessfully() {

        CreateMessageRequest request = CreateMessageRequest.builder()
                .content("Hello everyone!")
                .type(MessageType.TEXT)
                .build();

        Message savedMessage = Message.builder()
                .id(100L)
                .content(request.getContent())
                .type(MessageType.TEXT)
                .channel(channel)
                .sender(user)
                .edited(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(channelRepository.findById(channel.getId()))
                .thenReturn(Optional.of(channel));

        when(channelMemberRepository.existsByChannelAndUser(channel, user))
                .thenReturn(true);

        when(messageRepository.save(any(Message.class)))
                .thenReturn(savedMessage);

        MessageResponse response = messageService.sendMessage(
                channel.getId(),
                request,
                currentUser);

        assertNotNull(response);

        assertEquals(savedMessage.getId(), response.getId());
        assertEquals(savedMessage.getContent(), response.getContent());
        assertEquals(savedMessage.getType(), response.getType());

        assertEquals(user.getId(), response.getSenderId());
        assertEquals(user.getUsername(), response.getSenderUsername());
        assertEquals(user.getDisplayName(), response.getSenderDisplayName());

        verify(channelRepository).findById(channel.getId());
        verify(channelMemberRepository)
                .existsByChannelAndUser(channel, user);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void shouldThrowExceptionWhenChannelDoesNotExist() {

        CreateMessageRequest request = CreateMessageRequest.builder()
                .content("Hello")
                .build();

        when(channelRepository.findById(channel.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                ChannelDoesNotExistException.class,
                () -> messageService.sendMessage(
                        channel.getId(),
                        request,
                        currentUser));

        verify(channelRepository).findById(channel.getId());

        verify(channelMemberRepository, never())
                .existsByChannelAndUser(any(), any());

        verify(messageRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotChannelMember() {

        CreateMessageRequest request = CreateMessageRequest.builder()
                .content("Hello")
                .build();

        when(channelRepository.findById(channel.getId()))
                .thenReturn(Optional.of(channel));

        when(channelMemberRepository.existsByChannelAndUser(channel, user))
                .thenReturn(false);

        assertThrows(
                UserNotMemberOfChannelException.class,
                () -> messageService.sendMessage(
                        channel.getId(),
                        request,
                        currentUser));

        verify(channelRepository).findById(channel.getId());

        verify(channelMemberRepository)
                .existsByChannelAndUser(channel, user);

        verify(messageRepository, never())
                .save(any());
    }

    @Test
    void shouldDefaultMessageTypeToTextWhenTypeIsNull() {

        CreateMessageRequest request = CreateMessageRequest.builder()
                .content("Default message type")
                .type(null)
                .build();

        when(channelRepository.findById(channel.getId()))
                .thenReturn(Optional.of(channel));

        when(channelMemberRepository.existsByChannelAndUser(channel, user))
                .thenReturn(true);

        Message savedMessage = Message.builder()
                .id(1L)
                .content(request.getContent())
                .type(MessageType.TEXT)
                .channel(channel)
                .sender(user)
                .build();

        ArgumentCaptor<Message> captor =
                ArgumentCaptor.forClass(Message.class);

        when(messageRepository.save(any(Message.class)))
                .thenReturn(savedMessage);

        messageService.sendMessage(
                channel.getId(),
                request,
                currentUser);

        verify(messageRepository).save(captor.capture());

        Message captured = captor.getValue();

        assertEquals(MessageType.TEXT, captured.getType());
    }
}