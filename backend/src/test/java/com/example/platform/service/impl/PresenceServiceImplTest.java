package com.example.platform.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.example.platform.repository.UserRepository;
import com.example.platform.dto.PresenceResponse;
import com.example.platform.entity.User;
import com.example.platform.entity.UserStatus;
import com.example.platform.exception.UserNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PresenceServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PresenceServiceImpl presenceService;

    private User createUser() {
                return User.builder()
                                .id(1L)
                                .username("roshan")
                                .displayName("Roshan")
                                .email("roshan@example.com")
                                .status(UserStatus.OFFLINE)
                                .build();
        }

    @Test
    void shouldSetUserOnline() {
        User user = createUser();

        when(userRepository.findByUsername("roshan"))
        .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

        PresenceResponse response = presenceService.setUserOnline("roshan");

        assertEquals(UserStatus.ONLINE, user.getStatus());
        assertEquals(UserStatus.ONLINE, response.getStatus());
        assertEquals(user.getUsername(), response.getUsername());

        verify(userRepository).findByUsername("roshan");
        verify(userRepository).save(user);
    }

    @Test 
    void shouldSetUserOffline() {
        User user = createUser();
        user.setStatus(UserStatus.ONLINE);

        when(userRepository.findByUsername("roshan"))
        .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

        PresenceResponse response = presenceService.setUserOffline("roshan");
        assertEquals(UserStatus.OFFLINE, user.getStatus());
        assertEquals(UserStatus.OFFLINE, response.getStatus());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenSettingUserOnline() {
        when(userRepository.findByUsername("roshan"))
        .thenReturn(Optional.empty());
        assertThrows(
        UserNotFoundException.class,
        () -> presenceService.setUserOnline("roshan"));
        verify(userRepository).findByUsername("roshan");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenSettingUserOffline() {
        when(userRepository.findByUsername("roshan"))
        .thenReturn(Optional.empty());
        assertThrows(
        UserNotFoundException.class,
        () -> presenceService.setUserOffline("roshan"));
        verify(userRepository).findByUsername("roshan");
        verify(userRepository, never()).save(any());
    }
}
