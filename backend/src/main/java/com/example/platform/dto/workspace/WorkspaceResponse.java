package com.example.platform.dto.workspace;

import com.example.platform.entity.WorkspaceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResponse {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private WorkspaceType type;
    private String ownerUsername;
    private LocalDateTime createdAt;
}
