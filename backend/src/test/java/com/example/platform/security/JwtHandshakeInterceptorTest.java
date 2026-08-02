package com.example.platform.security;

import java.net.URI;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;


import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.WebSocketHandler;

import com.example.platform.exception.*;


@ExtendWith(MockitoExtension.class)
public class JwtHandshakeInterceptorTest {
    
    @InjectMocks
    private JwtHandshakeInterceptor jwtHandshakeInterceptor;

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
    private UserDetails userDetails;

    private HashMap<String, Object> attributes;
    private URI uri;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
    }

    @Test
    void shouldAllowHandshakeWhenTokenIsValid() {
        uri= URI.create("ws://localhost/ws?token=abc123");
        when(request.getURI()).thenReturn(uri);
        when(jwtService.extractUsername("abc123")).thenReturn("roshan");
        when(customUserDetailsService.loadUserByUsername("roshan"))
        .thenReturn(userDetails);
        when(jwtService.validateToken("abc123", userDetails)).thenReturn(true);

        boolean result= jwtHandshakeInterceptor.beforeHandshake(request, response, webSocketHandler, attributes);

        assertTrue(result);
        assertEquals("roshan", attributes.get("username"));

        verify(jwtService).extractUsername("abc123");
        verify(customUserDetailsService).loadUserByUsername("roshan");
        verify(jwtService).validateToken("abc123", userDetails);
    }

    @Test
    void shouldRejectHandshakeWhenQueryIsMissing() {
        uri=URI.create("ws://localhost/ws");
        when(request.getURI()).thenReturn(uri);
        boolean result = jwtHandshakeInterceptor.beforeHandshake(request, response, webSocketHandler, attributes);
        assertFalse(result);
        assertTrue(attributes.isEmpty());

        verifyNoInteractions(jwtService);
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    void shouldRejectHandshakeWhenTokenParameterIsMissing() {
        uri=URI.create("ws://localhost/ws?username=roshan");
        when(request.getURI()).thenReturn(uri);
        boolean result= jwtHandshakeInterceptor.beforeHandshake(request, response, webSocketHandler, attributes);
        assertFalse(result);
        assertTrue(attributes.isEmpty());

        verifyNoInteractions(jwtService);
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    void shouldRejectHandshakeWhenUsernameCannotBeExtracted() {
        uri= URI.create("ws://localhost/ws?token=abc123");
        when(request.getURI()).thenReturn(uri);
        when(jwtService.extractUsername("abc123")).thenReturn(null);
        boolean result = jwtHandshakeInterceptor.beforeHandshake(request, response, webSocketHandler, attributes);
        assertFalse(result);
        assertTrue(attributes.isEmpty());

        verify(jwtService, never()).validateToken("abc123", null);
        verify(customUserDetailsService, never()).loadUserByUsername(null);
    }

    @Test
    void shouldRejectHandshakeWhenTokenValidationFails() {
        uri= URI.create("ws://localhost/ws?token=abc123");
        when(request.getURI()).thenReturn(uri);
        when(jwtService.extractUsername("abc123")).thenReturn("roshan");
        when(customUserDetailsService.loadUserByUsername("roshan"))
        .thenReturn(userDetails);
        when(jwtService.validateToken("abc123", userDetails)).thenReturn(false);
        boolean result= jwtHandshakeInterceptor.beforeHandshake(request, response, webSocketHandler, attributes);

        assertFalse(result);
        assertTrue(attributes.isEmpty());

        verify(jwtService).extractUsername("abc123");
        verify(customUserDetailsService).loadUserByUsername("roshan");
        verify(jwtService).validateToken("abc123", userDetails);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        uri= URI.create("ws://localhost/ws?token=abc123");
        when(request.getURI()).thenReturn(uri);
        when(jwtService.extractUsername("abc123")).thenReturn("roshan");
        when(customUserDetailsService.loadUserByUsername("roshan"))
        .thenThrow(new UserNotFoundException(null));
        assertThrows(
                UserNotFoundException.class,
                () -> jwtHandshakeInterceptor.beforeHandshake(request, response, webSocketHandler, attributes));

        verify(jwtService,never()).validateToken(anyString(), any());
    }
}
