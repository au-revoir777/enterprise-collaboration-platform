package com.example.platform.security;

import com.example.platform.entity.Role;
import com.example.platform.entity.User;
import com.example.platform.entity.UserStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;
    private UserDetails userDetails;

    // utility method for injecting @Value fields
    private void injectField(
            Object target,
            String fieldName,
            Object value) throws Exception {

        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @BeforeEach
    void setUp() throws Exception {

        jwtService = new JwtService();

        // Generate a valid Base64-encoded 32-byte secret
        String secret = Base64.getEncoder()
                .encodeToString("12345678901234567890123456789012".getBytes());

        injectField(jwtService, "secretKey", secret);
        injectField(jwtService, "jwtExpiration", 86400000L); // 1 hour

        user = User.builder()
                .id(1L)
                .username("roshan")
                .email("roshan@example.com")
                .password("password")
                .displayName("Roshan")
                .role(Role.USER)
                .status(UserStatus.ONLINE)
                .active(true)
                .build();

        userDetails = new CustomUserDetails(user);
    }

    @Test
    void shouldGenerateValidToken() {

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);
        assertEquals(user.getUsername(), username);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        String token = jwtService.generateToken(user);
        User anotherUser = User.builder()
                .id(2L)
                .username("john")
                .email("john@example.com")
                .password("password")
                .displayName("John")
                .role(Role.USER)
                .status(UserStatus.ONLINE)
                .active(true)
                .build();

        UserDetails anotherUserDetails = new CustomUserDetails(anotherUser);
        assertFalse(jwtService.validateToken(token, anotherUserDetails));
    }

    @Test
    void shouldExtractExpirationDate() {
        String token = jwtService.generateToken(user);
        assertNotNull(jwtService.extractExpiration(token));
    }

}