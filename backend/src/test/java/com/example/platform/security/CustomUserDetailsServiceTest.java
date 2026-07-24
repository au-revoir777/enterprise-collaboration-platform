package com.example.platform.security;

import com.example.platform.entity.Role;
import com.example.platform.entity.User;
import com.example.platform.entity.UserStatus;
import com.example.platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsername() {

        User user = User.builder()
                .id(1L)
                .username("roshan")
                .email("roshan@example.com")
                .password("password")
                .displayName("Roshan")
                .role(Role.USER)
                .status(UserStatus.ONLINE)
                .active(true)
                .build();

        when(userRepository.findByUsername("roshan"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("roshan");

        assertNotNull(userDetails);
        assertEquals("roshan", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());

        verify(userRepository).findByUsername("roshan");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown")
        );

        assertEquals("Username not found.", exception.getMessage());

        verify(userRepository).findByUsername("unknown");
        verifyNoMoreInteractions(userRepository);
    }

}