package com.example.platform.dto.message;

import java.time.LocalDateTime;

import com.example.platform.entity.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private String content;
    private MessageType type;
    private Long senderId;
    private String senderUsername;
    private String senderDisplayName;
    private LocalDateTime createdAt;
    private boolean edited;
}
