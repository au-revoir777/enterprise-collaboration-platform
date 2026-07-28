package com.example.platform.service.impl;

import com.example.platform.entity.Channel;
import com.example.platform.entity.ChannelMember;
import com.example.platform.entity.User;
import com.example.platform.entity.Workspace;
import com.example.platform.repository.ChannelMemberRepository;
import com.example.platform.repository.ChannelRepository;
import com.example.platform.repository.WorkspaceMemberRepository;
import com.example.platform.repository.WorkspaceRepository;
import com.example.platform.dto.channel.ChannelResponse;
import com.example.platform.dto.channel.CreateChannelRequest;
import com.example.platform.service.ChannelService;
import com.example.platform.exception.WorkspaceNotFoundException;
import com.example.platform.exception.ChannelAlreadyExistsException;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService{

    // may have to centralise workspace authorization
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;

    private ChannelResponse mapToChannelResponse(Channel channel){
        return ChannelResponse.builder()
        .id(channel.getId())
        .name(channel.getName())
        .description(channel.getDescription())
        .type(channel.getType())
        .workspaceId(channel.getWorkspace().getId())
        .createdAt(channel.getCreatedAt())
        .build();
    }

    @Override
    public ChannelResponse createChannel(Long workspaceId, CreateChannelRequest request, User user){
        Workspace workspace = workspaceRepository.findById(workspaceId)
        .orElseThrow(()->new WorkspaceNotFoundException("Workspace does not exist."));
        if(!workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user)){
            throw new AccessDeniedException("You don't have access to this workspace.");
        }
        if(channelRepository.existsByNameAndWorkspace(request.getName(),workspace)){
                throw new ChannelAlreadyExistsException("Channel name already exists.");
        } 
        
        Channel channel=Channel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .workspace(workspace)
                .createdBy(user)
                .build();

                Channel savedChannel=channelRepository.save(channel);
                ChannelResponse response=mapToChannelResponse(savedChannel);

                ChannelMember member = ChannelMember.builder()
                .user(user)
                .channel(savedChannel)
                .addedBy(user)
                .build();
                
                channelMemberRepository.save(member);
                return response;
    }

}
