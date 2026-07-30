package com.example.platform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.platform.dto.channel.ChannelResponse;
import com.example.platform.dto.channel.CreateChannelRequest;
import com.example.platform.security.CustomUserDetails;
import com.example.platform.service.ChannelService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequestMapping("/api/workspaces/{workspaceId}/channels")
@RestController
@RequiredArgsConstructor
public class ChannelController {
        private final ChannelService channelService;

        @PostMapping
        public ResponseEntity<ChannelResponse> createChannel(
                        @PathVariable Long workspaceId,
                        @Valid @RequestBody CreateChannelRequest request,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                ChannelResponse response = channelService.createChannel(
                                workspaceId,
                                request,
                                userDetails.getUser());

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(response);
        }

        @GetMapping
        public ResponseEntity<List<ChannelResponse>> getWorkspaceChannels(
                        @PathVariable Long workspaceId,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                List<ChannelResponse> response = channelService.getWorkspaceChannels(
                                workspaceId,
                                userDetails.getUser());

                return ResponseEntity.ok(response);
        }
}
