package com.example.platform.dto.channel;

import java.time.LocalDateTime;

import com.example.platform.entity.ChannelType;
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
public class ChannelResponse {
    private Long id;
    private String name;
    private String description;
    private ChannelType type;
    private Long workspaceId;
    private LocalDateTime createdAt;
}
