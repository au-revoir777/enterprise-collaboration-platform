package com.example.platform.exception;

public class InvalidCredentialsException extends IllegalArgumentException{
    public InvalidCredentialsException (String message){
        super(message);
    }
    
}
