package com.example.platform.service;

import com.example.platform.entity.User;

import com.example.platform.dto.channel.ChannelResponse;
import com.example.platform.dto.channel.CreateChannelRequest;

public interface ChannelService {
    ChannelResponse createChannel(Long workspaceId, CreateChannelRequest request, User user);
}
