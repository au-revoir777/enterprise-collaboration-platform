package com.example.platform.service;

import com.example.platform.entity.User;

import com.example.platform.dto.channel.ChannelResponse;
import com.example.platform.dto.channel.CreateChannelRequest;

import java.util.List;

public interface ChannelService {
    ChannelResponse createChannel(Long workspaceId, CreateChannelRequest request, User user);
    List<ChannelResponse> getWorkspaceChannels(Long workspaceId, User user);
}
