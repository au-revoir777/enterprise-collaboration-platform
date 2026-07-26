package com.example.platform.dto.workspace;

import com.example.platform.entity.WorkspaceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkspaceRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    private WorkspaceType type;

    @Size(max = 500)
    private String logoUrl;
}
