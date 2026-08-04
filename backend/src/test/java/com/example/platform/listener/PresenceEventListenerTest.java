package com.example.platform.listener;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.example.platform.dto.PresenceResponse;
import com.example.platform.entity.UserStatus;
import com.example.platform.service.PresenceService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@ExtendWith(MockitoExtension.class)
class PresenceEventListenerTest {

    @Mock
    private PresenceService presenceService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private PresenceEventListener presenceEventListener;

    @Mock
    private SessionConnectedEvent connectedEvent;

    @Mock
    private SessionDisconnectEvent disconnectedEvent;

    @Mock
    private Principal principal;

    @Test
    void shouldSetUserOnlineAndBroadcastPresence() {
        PresenceResponse response = PresenceResponse.builder()
                .userId(1L)
                .username("roshan")
                .status(UserStatus.ONLINE)
                .build();

        when(connectedEvent.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn("roshan");

        when(presenceService.setUserOnline("roshan"))
                .thenReturn(response);

        presenceEventListener.handleSessionConnected(connectedEvent);

        verify(presenceService).setUserOnline("roshan");

        verify(messagingTemplate).convertAndSend(
                "/topic/presence",
                response);

        verifyNoMoreInteractions(
                presenceService,
                messagingTemplate);
    }

    @Test
    void shouldDoNothingWhenConnectedPrincipalIsNull() {
        when(connectedEvent.getUser()).thenReturn(null);
        presenceEventListener.handleSessionConnected(connectedEvent);
        verifyNoInteractions(
                presenceService,
                messagingTemplate);
    }

    @Test
    void shouldSetUserOfflineAndBroadcastPresence() {
        PresenceResponse response = PresenceResponse.builder()
                .userId(1L)
                .username("roshan")
                .status(UserStatus.OFFLINE)
                .build();

        when(disconnectedEvent.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn("roshan");

        when(presenceService.setUserOffline("roshan"))
                .thenReturn(response);

        presenceEventListener.handleSessionDisconnected(disconnectedEvent);
        verify(presenceService).setUserOffline("roshan");

        verify(messagingTemplate).convertAndSend(
                "/topic/presence",
                response);

        verifyNoMoreInteractions(
                presenceService,
                messagingTemplate);
    }

    @Test
    void shouldDoNothingWhenDisconnectedPrincipalIsNull() {
        when(disconnectedEvent.getUser()).thenReturn(null);
        presenceEventListener.handleSessionDisconnected(disconnectedEvent);
        verifyNoInteractions(
                presenceService,
                messagingTemplate);
    }
}
