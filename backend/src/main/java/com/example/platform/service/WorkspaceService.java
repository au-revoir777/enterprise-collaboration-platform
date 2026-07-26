package com.example.platform.service;

import com.example.platform.entity.User;
import com.example.platform.dto.workspace.CreateWorkspaceRequest;
import com.example.platform.dto.workspace.WorkspaceResponse;

public interface WorkspaceService {
    WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, User owner);
}
