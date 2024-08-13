package com.tinqinacademy.authentication.api.exceptions;

import com.tinqinacademy.authentication.api.messages.ExceptionMessages;

public class EmailAlreadyExistsException extends RuntimeException {

    private final String message = ExceptionMessages.EMAIL_TAKEN;
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
