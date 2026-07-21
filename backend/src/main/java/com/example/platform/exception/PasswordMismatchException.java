package com.example.platform.exception;

public class PasswordMismatchException extends IllegalArgumentException{
    public PasswordMismatchException (String message){
        super(message);
    }
}
