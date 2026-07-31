package com.example.platform.exception;

public class ChannelDoesNotExistException extends RuntimeException{
    public ChannelDoesNotExistException (String message){
        super(message);
    }
}
