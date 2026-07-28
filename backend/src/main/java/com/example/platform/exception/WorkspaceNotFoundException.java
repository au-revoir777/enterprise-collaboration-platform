package com.example.platform.exception;

public class WorkspaceNotFoundException extends RuntimeException{
    public WorkspaceNotFoundException(String message){
        super(message);
    }
}
