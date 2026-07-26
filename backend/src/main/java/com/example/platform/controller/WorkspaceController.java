package com.example.platform.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.platform.dto.workspace.CreateWorkspaceRequest;
import com.example.platform.dto.workspace.WorkspaceResponse;
import com.example.platform.security.CustomUserDetails;
import com.example.platform.service.WorkspaceService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import lombok.RequiredArgsConstructor;

@RequestMapping("/api/workspaces")
@RestController
@RequiredArgsConstructor
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        WorkspaceResponse response = workspaceService.createWorkspace(request, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
