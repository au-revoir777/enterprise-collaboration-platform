package com.example.platform.exception;

public class UserNotMemberOfChannelException extends RuntimeException {
    public UserNotMemberOfChannelException (String message){
        super(message);
    }
}
