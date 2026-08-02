package com.example.platform.service.impl;

import com.example.platform.dto.message.CreateMessageRequest;
import com.example.platform.dto.message.MessageResponse;
import com.example.platform.dto.websocket.ChatMessageRequest;
import com.example.platform.repository.ChannelMemberRepository;
import com.example.platform.repository.ChannelRepository;
import com.example.platform.repository.MessageRepository;
import com.example.platform.repository.UserRepository;
import com.example.platform.security.CustomUserDetails;
import com.example.platform.service.MessageService;

import com.example.platform.exception.ChannelDoesNotExistException;
import com.example.platform.exception.UserNotMemberOfChannelException;
import com.example.platform.entity.Channel;
import com.example.platform.entity.Message;
import com.example.platform.entity.MessageType;

import lombok.RequiredArgsConstructor;

import com.example.platform.entity.User;

import org.springframework.stereotype.Service;
import com.example.platform.exception.UserNotFoundException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;

    private Message mapToMessage(CreateMessageRequest request, Channel channel, User user){
        return Message.builder()
        .content(request.getContent())
        .type(request.getType() == null
        ? MessageType.TEXT
        : request.getType())
        .channel(channel)
        .sender(user)
        .build();
    }

      private MessageResponse sendMessage(Long channelId, CreateMessageRequest request, User user) {
        Channel channel=channelRepository.findById(channelId)
        .orElseThrow(()->new ChannelDoesNotExistException("Channel does not exist."));
        if(!channelMemberRepository.existsByChannelAndUser(channel, user)){
            throw new UserNotMemberOfChannelException("You don't have access to this channel.");
        }
        Message message=mapToMessage(request, channel, user);
        Message savedMessage = messageRepository.save(message);
        return mapToMessageResponse(savedMessage);
    }

    private MessageResponse mapToMessageResponse(Message message){
        return MessageResponse.builder()
        .id(message.getId())
        .content(message.getContent())
        .type(message.getType())
        .senderId(message.getSender().getId())
        .senderUsername(message.getSender().getUsername())
        .senderDisplayName(message.getSender().getDisplayName())
        .createdAt(message.getCreatedAt())
        .edited(message.isEdited())
        .build();
    }

    @Override
    public MessageResponse sendMessage(Long channelId, CreateMessageRequest request, CustomUserDetails currentUser) {
        return sendMessage(channelId, request, currentUser.getUser());
    }

    @Override
    public List<MessageResponse> getMessages(Long channelId, CustomUserDetails currentUser){
        Channel channel = channelRepository.findById(channelId)
        .orElseThrow(()->new ChannelDoesNotExistException("Channel does not exist."));
        User user=currentUser.getUser();
        if(!channelMemberRepository.existsByChannelAndUser(channel, user)){
            throw new UserNotMemberOfChannelException("You don't have access to this channel.");
        }

        return messageRepository
            .findByChannelOrderByCreatedAtAsc(channel)
            .stream()
            .map(this::mapToMessageResponse)
            .toList();
    }

    @Override
    public MessageResponse sendMessage(ChatMessageRequest request, String username){
        CreateMessageRequest createMessageRequest = CreateMessageRequest.builder()
        .content(request.getContent())
        .type(request.getType())
        .build();

        User user = userRepository.findByUsername(username)
        .orElseThrow(()->new UserNotFoundException("Username not found"));

        return sendMessage(request.getChannelId(), createMessageRequest, user);
    }

}
