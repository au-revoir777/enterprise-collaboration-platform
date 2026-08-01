package com.example.platform.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;

@ExtendWith(MockitoExtension.class)
class JwtHandshakeInterceptorTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebSocketHandler webSocketHandler;

    @Mock
    private HttpHeaders headers;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtHandshakeInterceptor interceptor;

    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {

        attributes = new HashMap<>();

        when(request.getHeaders()).thenReturn(headers);
    }

    @Test
    void shouldAllowHandshakeWhenBearerTokenIsValid() {

        String token = "jwt-token";

        when(headers.getFirst(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(jwtService.extractUsername(token))
                .thenReturn("roshan");

        when(customUserDetailsService.loadUserByUsername("roshan"))
                .thenReturn(userDetails);

        when(jwtService.validateToken(token, userDetails))
                .thenReturn(true);

        boolean result = interceptor.beforeHandshake(
                request,
                response,
                webSocketHandler,
                attributes);

        assertTrue(result);

        assertEquals("roshan", attributes.get("username"));

        verify(jwtService).extractUsername(token);
        verify(customUserDetailsService)
                .loadUserByUsername("roshan");
        verify(jwtService)
                .validateToken(token, userDetails);
    }

    @Test
    void shouldRejectHandshakeWhenAuthorizationHeaderIsMissing() {

        when(headers.getFirst(HttpHeaders.AUTHORIZATION))
                .thenReturn(null);

        boolean result = interceptor.beforeHandshake(
                request,
                response,
                webSocketHandler,
                attributes);

        assertFalse(result);

        assertTrue(attributes.isEmpty());

        verifyNoInteractions(jwtService);
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    void shouldRejectHandshakeWhenAuthorizationHeaderIsNotBearer() {

        when(headers.getFirst(HttpHeaders.AUTHORIZATION))
                .thenReturn("Basic abc123");

        boolean result = interceptor.beforeHandshake(
                request,
                response,
                webSocketHandler,
                attributes);

        assertFalse(result);

        assertTrue(attributes.isEmpty());

        verifyNoInteractions(jwtService);
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    void shouldRejectHandshakeWhenTokenIsInvalid() {

        String token = "jwt-token";

        when(headers.getFirst(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(jwtService.extractUsername(token))
                .thenReturn("roshan");

        when(customUserDetailsService.loadUserByUsername("roshan"))
                .thenReturn(userDetails);

        when(jwtService.validateToken(token, userDetails))
                .thenReturn(false);

        boolean result = interceptor.beforeHandshake(
                request,
                response,
                webSocketHandler,
                attributes);

        assertFalse(result);

        assertTrue(attributes.isEmpty());

        verify(jwtService).extractUsername(token);
        verify(customUserDetailsService)
                .loadUserByUsername("roshan");
        verify(jwtService)
                .validateToken(token, userDetails);
    }

    @Test
    void shouldStoreUsernameInHandshakeAttributes() {

        String token = "jwt-token";

        when(headers.getFirst(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(jwtService.extractUsername(token))
                .thenReturn("test123");

        when(customUserDetailsService.loadUserByUsername("test123"))
                .thenReturn(userDetails);

        when(jwtService.validateToken(token, userDetails))
                .thenReturn(true);

        interceptor.beforeHandshake(
                request,
                response,
                webSocketHandler,
                attributes);

        assertEquals("test123", attributes.get("username"));
    }

    @Test
    void afterHandshakeShouldDoNothing() {

        interceptor.afterHandshake(
                request,
                response,
                webSocketHandler,
                null);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(customUserDetailsService);
    }
}