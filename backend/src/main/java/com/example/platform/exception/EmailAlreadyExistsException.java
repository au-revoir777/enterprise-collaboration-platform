package com.example.platform.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}

// later RestControllerAdvice can translate this into proper http response