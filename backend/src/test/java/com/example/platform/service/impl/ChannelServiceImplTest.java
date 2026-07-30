package com.example.platform.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.example.platform.dto.channel.ChannelResponse;
import com.example.platform.dto.channel.CreateChannelRequest;
import com.example.platform.entity.ChannelType;
import com.example.platform.entity.Workspace;
import com.example.platform.entity.WorkspaceType;
import com.example.platform.exception.ChannelAlreadyExistsException;
import com.example.platform.exception.WorkspaceNotFoundException;
import com.example.platform.repository.ChannelMemberRepository;
import com.example.platform.repository.ChannelRepository;
import com.example.platform.repository.WorkspaceMemberRepository;
import com.example.platform.repository.WorkspaceRepository;
import com.example.platform.entity.User;
import com.example.platform.entity.Channel;
import com.example.platform.entity.ChannelMember;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceImplTest {
        private User createUser() {
                return User.builder()
                                .id(1L)
                                .username("roshan")
                                .displayName("Roshan")
                                .email("roshan@example.com")
                                .build();
        }

        private Workspace createWorkspace(User owner) {
                return Workspace.builder()
                                .id(1L)
                                .name("Platform")
                                .description("Platform Workspace")
                                .owner(owner)
                                .type(WorkspaceType.PRIVATE)
                                .build();
        }

        private CreateChannelRequest createChannelRequest() {
                return CreateChannelRequest.builder()
                                .name("backend")
                                .description("Backend discussions")
                                .type(ChannelType.PUBLIC)
                                .build();
        }

        private Channel createChannel(User user, Workspace workspace) {
                return Channel.builder()
                                .id(1L)
                                .name("backend")
                                .description("Backend discussions")
                                .type(ChannelType.PUBLIC)
                                .workspace(workspace)
                                .createdBy(user)
                                .build();
        }

        @Mock
        private WorkspaceRepository workspaceRepository;

        @Mock
        private WorkspaceMemberRepository workspaceMemberRepository;

        @Mock
        private ChannelRepository channelRepository;

        @Mock
        private ChannelMemberRepository channelMemberRepository;

        @InjectMocks
        private ChannelServiceImpl channelService;

        @Test
        void shouldCreateChannelSuccessfully() {

                User user = createUser();
                Workspace workspace = createWorkspace(user);
                CreateChannelRequest request = createChannelRequest();
                Channel savedChannel = createChannel(user, workspace);

                when(workspaceRepository.findById(workspace.getId()))
                                .thenReturn(Optional.of(workspace));

                when(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user))
                                .thenReturn(true);

                when(channelRepository.existsByNameAndWorkspace(request.getName(), workspace))
                                .thenReturn(false);

                when(channelRepository.save(any(Channel.class)))
                                .thenReturn(savedChannel);

                ChannelResponse response = channelService.createChannel(workspace.getId(), request, user);

                assertNotNull(response);

                assertEquals(savedChannel.getId(), response.getId());
                assertEquals(savedChannel.getName(), response.getName());
                assertEquals(savedChannel.getDescription(), response.getDescription());
                assertEquals(savedChannel.getType(), response.getType());
                assertEquals(workspace.getId(), response.getWorkspaceId());

                verify(channelRepository).save(any(Channel.class));
                verify(channelMemberRepository).save(any(ChannelMember.class));
        }

        @Test
        void shouldAutomaticallyAddCreatorAsChannelMember() {

                User user = createUser();
                Workspace workspace = createWorkspace(user);
                CreateChannelRequest request = createChannelRequest();
                Channel savedChannel = createChannel(user, workspace);

                when(workspaceRepository.findById(workspace.getId()))
                                .thenReturn(Optional.of(workspace));

                when(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user))
                                .thenReturn(true);

                when(channelRepository.existsByNameAndWorkspace(request.getName(), workspace))
                                .thenReturn(false);

                when(channelRepository.save(any(Channel.class)))
                                .thenReturn(savedChannel);

                channelService.createChannel(workspace.getId(), request, user);

                ArgumentCaptor<ChannelMember> captor = ArgumentCaptor.forClass(ChannelMember.class);

                verify(channelMemberRepository).save(captor.capture());

                ChannelMember member = captor.getValue();

                assertEquals(user, member.getUser());
                assertEquals(savedChannel, member.getChannel());
                assertEquals(user, member.getAddedBy());
        }

        @Test
        void shouldThrowWorkspaceNotFoundException() {

                User user = createUser();
                CreateChannelRequest request = createChannelRequest();

                when(workspaceRepository.findById(1L))
                                .thenReturn(Optional.empty());

                assertThrows(
                                WorkspaceNotFoundException.class,
                                () -> channelService.createChannel(1L, request, user));

                verify(workspaceRepository).findById(1L);

                verifyNoInteractions(
                                workspaceMemberRepository,
                                channelRepository,
                                channelMemberRepository);
        }

        @Test
        void shouldThrowAccessDeniedExceptionWhenUserIsNotWorkspaceMember() {

                User user = createUser();
                Workspace workspace = createWorkspace(user);
                CreateChannelRequest request = createChannelRequest();

                when(workspaceRepository.findById(workspace.getId()))
                                .thenReturn(Optional.of(workspace));

                when(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user))
                                .thenReturn(false);

                assertThrows(
                                org.springframework.security.access.AccessDeniedException.class,
                                () -> channelService.createChannel(workspace.getId(), request, user));

                verify(channelRepository, never()).save(any());
                verify(channelMemberRepository, never()).save(any());
        }

        @Test
        void shouldThrowChannelAlreadyExistsException() {

                User user = createUser();
                Workspace workspace = createWorkspace(user);
                CreateChannelRequest request = createChannelRequest();

                when(workspaceRepository.findById(workspace.getId()))
                                .thenReturn(Optional.of(workspace));

                when(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user))
                                .thenReturn(true);

                when(channelRepository.existsByNameAndWorkspace(request.getName(), workspace))
                                .thenReturn(true);

                assertThrows(
                                ChannelAlreadyExistsException.class,
                                () -> channelService.createChannel(workspace.getId(), request, user));

                verify(channelRepository, never()).save(any(Channel.class));
                verify(channelMemberRepository, never()).save(any(ChannelMember.class));
        }

        @Test
        void shouldReturnAllChannels() {

                User user = createUser();
                Workspace workspace = createWorkspace(user);

                Channel channel1 = Channel.builder()
                                .id(1L)
                                .name("general")
                                .description("General discussions")
                                .type(ChannelType.PUBLIC)
                                .workspace(workspace)
                                .createdBy(user)
                                .build();

                Channel channel2 = Channel.builder()
                                .id(2L)
                                .name("backend")
                                .description("Backend discussions")
                                .type(ChannelType.PRIVATE)
                                .workspace(workspace)
                                .createdBy(user)
                                .build();

                when(workspaceRepository.findById(workspace.getId()))
                                .thenReturn(Optional.of(workspace));

                when(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user))
                                .thenReturn(true);

                when(channelRepository.findByWorkspace(workspace))
                                .thenReturn(List.of(channel1, channel2));

                List<ChannelResponse> response = channelService.getWorkspaceChannels(workspace.getId(), user);

                assertNotNull(response);
                assertEquals(2, response.size());

                assertEquals("general", response.get(0).getName());
                assertEquals("backend", response.get(1).getName());

                verify(workspaceRepository).findById(workspace.getId());
                verify(workspaceMemberRepository)
                                .existsByWorkspaceAndUser(workspace, user);
                verify(channelRepository).findByWorkspace(workspace);
        }

        @Test
        void shouldReturnEmptyListWhenWorkspaceHasNoChannels() {

                User user = createUser();
                Workspace workspace = createWorkspace(user);

                when(workspaceRepository.findById(workspace.getId()))
                                .thenReturn(Optional.of(workspace));

                when(workspaceMemberRepository.existsByWorkspaceAndUser(workspace, user))
                                .thenReturn(true);

                when(channelRepository.findByWorkspace(workspace))
                                .thenReturn(List.of());

                List<ChannelResponse> response = channelService.getWorkspaceChannels(workspace.getId(), user);

                assertNotNull(response);
                assertTrue(response.isEmpty());

                verify(channelRepository).findByWorkspace(workspace);
        }

        
}
