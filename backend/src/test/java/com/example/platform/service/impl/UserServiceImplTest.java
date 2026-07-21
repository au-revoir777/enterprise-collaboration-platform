package com.example.platform.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.platform.dto.RegisterRequest;
import com.example.platform.dto.UserResponse;
import com.example.platform.entity.UserStatus;
import com.example.platform.exception.EmailAlreadyExistsException;
import com.example.platform.exception.PasswordMismatchException;
import com.example.platform.exception.UsernameAlreadyExistsException;
import com.example.platform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.platform.entity.User;
import com.example.platform.entity.Role;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private RegisterRequest createValidRequest() {
        return RegisterRequest.builder()
                .username("roshan")
                .displayName("Roshan")
                .email("roshan@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();
    }

    private User createSavedUser() {
        return User.builder()
                .id(1L)
                .username("roshan")
                .displayName("Roshan")
                .email("roshan@example.com")
                .password("hashedPassword")
                .role(Role.USER)
                .status(UserStatus.OFFLINE)
                .active(true)
                .build();
    }

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void shouldThrowEmailAlreadyExistsException() {

        // Arrange
        RegisterRequest request = createValidRequest();

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.register(request)
        );

        // Verify
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowUsernameAlreadyExistsException() {
        RegisterRequest request = createValidRequest();
        
        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername()))
                .thenReturn(true);

        assertThrows(
            UsernameAlreadyExistsException.class,
                () -> userService.register(request)
        );

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).existsByUsername(request.getUsername());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        
    }

    @Test
    void shouldThrowPasswordMismatchException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("roshan")
                .displayName("Roshan")
                .email("roshan@example.com")
                .password("password123")
                .confirmPassword("password1234")
                .build();
                
        when(userRepository.existsByEmail(request.getEmail()))
        .thenReturn(false);

        when(userRepository.existsByUsername(request.getUsername()))
        .thenReturn(false); 

        assertThrows(
            PasswordMismatchException.class,
                () -> userService.register(request)
        );

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).existsByUsername(request.getUsername());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldHashPasswordBeforeSaving() {
        RegisterRequest request = createValidRequest();

        when(userRepository.existsByEmail(request.getEmail()))
        .thenReturn(false);

        when(userRepository.existsByUsername(request.getUsername()))
        .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
        .thenReturn("hashedPassword");

        User savedUser = createSavedUser();

        when(userRepository.save(any(User.class)))
        .thenReturn(savedUser);

        userService.register(request);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User capturedUser = captor.getValue();

        assertEquals("hashedPassword", capturedUser.getPassword());
        assertNotEquals(request.getPassword(),capturedUser.getPassword());
        assertEquals(request.getUsername(), capturedUser.getUsername());
        assertEquals(request.getEmail(), capturedUser.getEmail());
        assertEquals(request.getDisplayName(), capturedUser.getDisplayName());

        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).existsByUsername(request.getUsername());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = createValidRequest();

        User savedUser = createSavedUser();

        when(userRepository.existsByEmail(request.getEmail()))
        .thenReturn(false);

        when(userRepository.existsByUsername(request.getUsername()))
        .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
        .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
        .thenReturn(savedUser);

        UserResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("roshan", response.getUsername());
        assertEquals("Roshan", response.getDisplayName());
        assertEquals("roshan@example.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
        assertEquals(UserStatus.OFFLINE, response.getStatus());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(request.getPassword());
    }

}
