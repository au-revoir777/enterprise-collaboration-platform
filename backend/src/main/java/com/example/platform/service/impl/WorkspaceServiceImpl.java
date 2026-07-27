package com.example.platform.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.platform.entity.User;
import com.example.platform.entity.Workspace;
import com.example.platform.entity.WorkspaceMember;
import com.example.platform.entity.WorkspaceRole;
import com.example.platform.dto.workspace.CreateWorkspaceRequest;
import com.example.platform.dto.workspace.WorkspaceResponse;
import com.example.platform.repository.WorkspaceMemberRepository;
import com.example.platform.repository.WorkspaceRepository;
import com.example.platform.service.WorkspaceService;


import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    // as the project grows, consider extracting the mapping logic into a dedicated
    // mapper class
    private WorkspaceResponse mapToWorkspaceResponse(Workspace workspace) {
        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .logoUrl(workspace.getLogoUrl())
                .type(workspace.getType())
                .ownerUsername(workspace.getOwner().getUsername())
                .createdAt(workspace.getCreatedAt())
                .build();
    }

    @Override
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, User owner) {
        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .logoUrl(request.getLogoUrl())
                .owner(owner)
                .build();

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .user(owner)
                .workspace(savedWorkspace)
                .role(WorkspaceRole.OWNER)
                .build();

        workspaceMemberRepository.save(workspaceMember);
        WorkspaceResponse response = mapToWorkspaceResponse(savedWorkspace);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getMyWorkspaces(User user) {
        return workspaceMemberRepository.findByUser(user)
                .stream()
                .map(WorkspaceMember::getWorkspace)
                .map(this::mapToWorkspaceResponse)
                .toList();
    }
}
