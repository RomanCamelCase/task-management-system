package com.gmail.romkatsis.taskmanagementsystem.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
