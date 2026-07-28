package com.example.platform.dto.channel;

import com.example.platform.entity.ChannelType;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotBlank;
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
public class CreateChannelRequest {
    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 300)
    private String description;

    @NotNull
    private ChannelType type;
}
