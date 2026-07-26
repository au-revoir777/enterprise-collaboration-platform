package com.example.platform.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.platform.dto.workspace.CreateWorkspaceRequest;
import com.example.platform.dto.workspace.WorkspaceResponse;
import com.example.platform.entity.User;
import com.example.platform.entity.Workspace;
import com.example.platform.entity.WorkspaceMember;
import com.example.platform.entity.WorkspaceRole;
import com.example.platform.entity.WorkspaceType;
import com.example.platform.repository.WorkspaceMemberRepository;
import com.example.platform.repository.WorkspaceRepository;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceImplTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private WorkspaceServiceImpl workspaceService;

    private User createOwner() {
        return User.builder()
                .id(1L)
                .username("roshan")
                .displayName("Roshan")
                .email("roshan@example.com")
                .build();
    }

    private CreateWorkspaceRequest createRequest() {
        return CreateWorkspaceRequest.builder()
                .name("Backend Team")
                .description("Backend Development")
                .type(WorkspaceType.PRIVATE)
                .logoUrl("logo.png")
                .build();
    }

    private Workspace createSavedWorkspace(User owner) {
        return Workspace.builder()
                .id(10L)
                .name("Backend Team")
                .description("Backend Development")
                .type(WorkspaceType.PRIVATE)
                .logoUrl("logo.png")
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateWorkspaceSuccessfully() {

        // Arrange
        User owner = createOwner();
        CreateWorkspaceRequest request = createRequest();

        Workspace savedWorkspace = createSavedWorkspace(owner);

        when(workspaceRepository.save(any(Workspace.class)))
                .thenReturn(savedWorkspace);

        // Act
        WorkspaceResponse response =
                workspaceService.createWorkspace(request, owner);

        // Assert
        assertNotNull(response);

        assertEquals(savedWorkspace.getId(), response.getId());
        assertEquals(savedWorkspace.getName(), response.getName());
        assertEquals(savedWorkspace.getDescription(), response.getDescription());
        assertEquals(savedWorkspace.getLogoUrl(), response.getLogoUrl());
        assertEquals(savedWorkspace.getType(), response.getType());
        assertEquals(owner.getUsername(), response.getOwnerUsername());

        verify(workspaceRepository).save(any(Workspace.class));
        verify(workspaceMemberRepository).save(any(WorkspaceMember.class));
    }

    @Test
    void shouldSaveWorkspaceWithCorrectValues() {

        User owner = createOwner();
        CreateWorkspaceRequest request = createRequest();

        Workspace savedWorkspace = createSavedWorkspace(owner);

        when(workspaceRepository.save(any(Workspace.class)))
                .thenReturn(savedWorkspace);

        workspaceService.createWorkspace(request, owner);

        ArgumentCaptor<Workspace> captor =
                ArgumentCaptor.forClass(Workspace.class);

        verify(workspaceRepository).save(captor.capture());

        Workspace captured = captor.getValue();

        assertEquals(request.getName(), captured.getName());
        assertEquals(request.getDescription(), captured.getDescription());
        assertEquals(request.getType(), captured.getType());
        assertEquals(request.getLogoUrl(), captured.getLogoUrl());
        assertEquals(owner, captured.getOwner());
    }

    @Test
    void shouldCreateOwnerWorkspaceMember() {

        User owner = createOwner();
        CreateWorkspaceRequest request = createRequest();

        Workspace savedWorkspace = createSavedWorkspace(owner);

        when(workspaceRepository.save(any(Workspace.class)))
                .thenReturn(savedWorkspace);

        workspaceService.createWorkspace(request, owner);

        ArgumentCaptor<WorkspaceMember> captor =
                ArgumentCaptor.forClass(WorkspaceMember.class);

        verify(workspaceMemberRepository).save(captor.capture());

        WorkspaceMember member = captor.getValue();

        assertEquals(owner, member.getUser());
        assertEquals(savedWorkspace, member.getWorkspace());
        assertEquals(WorkspaceRole.OWNER, member.getRole());
        assertTrue(member.isActive());
    }
}