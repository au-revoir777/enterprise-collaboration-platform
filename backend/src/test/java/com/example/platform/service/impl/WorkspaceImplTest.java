package com.example.platform.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

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

        private WorkspaceMember createWorkspaceMember(User owner, Workspace workspace) {
                return WorkspaceMember.builder()
                                .user(owner)
                                .workspace(workspace)
                                .role(WorkspaceRole.OWNER)
                                .active(true)
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
                WorkspaceResponse response = workspaceService.createWorkspace(request, owner);

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

                ArgumentCaptor<Workspace> captor = ArgumentCaptor.forClass(Workspace.class);

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

                ArgumentCaptor<WorkspaceMember> captor = ArgumentCaptor.forClass(WorkspaceMember.class);

                verify(workspaceMemberRepository).save(captor.capture());

                WorkspaceMember member = captor.getValue();

                assertEquals(owner, member.getUser());
                assertEquals(savedWorkspace, member.getWorkspace());
                assertEquals(WorkspaceRole.OWNER, member.getRole());
                assertTrue(member.isActive());
        }

        @Test
        void shouldReturnEmptyWorkspaceList() {

                // Arrange
                User owner = createOwner();

                when(workspaceMemberRepository.findByUser(owner))
                                .thenReturn(List.of());

                // Act
                List<WorkspaceResponse> response = workspaceService.getMyWorkspaces(owner);

                // Assert
                assertNotNull(response);
                assertTrue(response.isEmpty());

                verify(workspaceMemberRepository).findByUser(owner);
        }

        @Test
        void shouldReturnOneWorkspace() {

                // Arrange
                User owner = createOwner();

                Workspace workspace = createSavedWorkspace(owner);

                WorkspaceMember member = createWorkspaceMember(owner, workspace);

                when(workspaceMemberRepository.findByUser(owner))
                                .thenReturn(List.of(member));

                // Act
                List<WorkspaceResponse> response = workspaceService.getMyWorkspaces(owner);

                // Assert
                assertNotNull(response);
                assertEquals(1, response.size());

                WorkspaceResponse workspaceResponse = response.getFirst();

                assertEquals(workspace.getId(), workspaceResponse.getId());
                assertEquals(workspace.getName(), workspaceResponse.getName());
                assertEquals(workspace.getDescription(), workspaceResponse.getDescription());
                assertEquals(workspace.getLogoUrl(), workspaceResponse.getLogoUrl());
                assertEquals(workspace.getType(), workspaceResponse.getType());
                assertEquals(owner.getUsername(), workspaceResponse.getOwnerUsername());

                verify(workspaceMemberRepository).findByUser(owner);
        }

        @Test
        void shouldReturnMultipleWorkspaces() {

                // Arrange
                User owner = createOwner();

                Workspace workspace1 = Workspace.builder()
                                .id(1L)
                                .name("Backend")
                                .description("Backend Team")
                                .type(WorkspaceType.PRIVATE)
                                .logoUrl("backend.png")
                                .owner(owner)
                                .build();

                Workspace workspace2 = Workspace.builder()
                                .id(2L)
                                .name("Frontend")
                                .description("Frontend Team")
                                .type(WorkspaceType.PUBLIC)
                                .logoUrl("frontend.png")
                                .owner(owner)
                                .build();

                WorkspaceMember member1 = createWorkspaceMember(owner, workspace1);

                WorkspaceMember member2 = createWorkspaceMember(owner, workspace2);

                when(workspaceMemberRepository.findByUser(owner))
                                .thenReturn(List.of(member1, member2));

                // Act
                List<WorkspaceResponse> response = workspaceService.getMyWorkspaces(owner);

                // Assert
                assertEquals(2, response.size());

                assertEquals("Backend", response.get(0).getName());
                assertEquals("Frontend", response.get(1).getName());

                assertEquals(owner.getUsername(),
                                response.get(0).getOwnerUsername());

                assertEquals(owner.getUsername(),
                                response.get(1).getOwnerUsername());

                verify(workspaceMemberRepository).findByUser(owner);
        }

        @Test
        void shouldQueryWorkspaceMemberRepositoryOnlyOnce() {
                User owner = createOwner();
                when(workspaceMemberRepository.findByUser(owner))
                                .thenReturn(List.of());
                workspaceService.getMyWorkspaces(owner);
                verify(workspaceMemberRepository, times(1))
                                .findByUser(owner);
                verifyNoMoreInteractions(
                                workspaceRepository,
                                workspaceMemberRepository);
        }
}