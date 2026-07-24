package com.example.platform.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.platform.repository.UserRepository;

import com.example.platform.entity.User;


import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        
        User user=userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(
                    "Username not found."
                ));

        return new CustomUserDetails(user);
        
    }
}
