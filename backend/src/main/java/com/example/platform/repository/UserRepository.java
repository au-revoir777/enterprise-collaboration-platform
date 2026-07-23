package com.example.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.platform.entity.User;

import java.util.Optional;



public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email); //whether the email already exists
    boolean existsByUsername(String username); //whether the username already exists
}
