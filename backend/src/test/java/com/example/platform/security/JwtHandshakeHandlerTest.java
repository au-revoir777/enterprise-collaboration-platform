package com.example.platform.security;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.http.server.ServerHttpRequest;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class JwtHandshakeHandlerTest {
    @InjectMocks
    private JwtHandshakeHandler jwtHandshakeHandler;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private WebSocketHandler webSocketHandler;

    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
    }

    @Test
    void shouldCreatePrincipalWhenUsernameIsPresent() {
        attributes.put("username", "roshan");
        Principal principal = jwtHandshakeHandler.determineUser(request, webSocketHandler, attributes);
        assertNotNull(principal);
        assertEquals("roshan", principal.getName());
    }

    @Test
    void shouldReturnNullWhenUsernameIsMissing() {
        Principal principal = jwtHandshakeHandler.determineUser(request, webSocketHandler, attributes);
        assertNull(principal);
    }
}
