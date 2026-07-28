package com.example.platform.exception;

public class ChannelAlreadyExistsException extends RuntimeException{
    public ChannelAlreadyExistsException(String message){
        super(message);
    }
}
