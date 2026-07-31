package com.example.platform.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.example.platform.entity.MessageType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMessageRequest {

    @NotBlank(message = "Message content cannot be empty.")

    @Size(max = 4000, message = "Message content cannot exceed 4000 characters.")
    private String content;

    private MessageType type;
}