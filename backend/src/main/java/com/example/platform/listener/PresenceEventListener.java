package com.example.platform.listener;

import lombok.RequiredArgsConstructor;

import com.example.platform.dto.PresenceResponse;
import com.example.platform.service.PresenceService;

import java.security.Principal;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class PresenceEventListener {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {

        Principal principal = event.getUser();

        if (principal != null) {
            PresenceResponse response = presenceService.setUserOnline(principal.getName());

            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    response);
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {

        Principal principal = event.getUser();

        if (principal != null) {
            PresenceResponse response = presenceService.setUserOffline(principal.getName());

            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    response);
        }
    }

}
