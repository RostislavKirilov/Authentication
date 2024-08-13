package com.tinqinacademy.authentication.api.exceptions;

import com.tinqinacademy.authentication.api.messages.ExceptionMessages;
import lombok.Getter;

@Getter
public class UsernameAlreadyExistsException extends RuntimeException {

    private final String message = ExceptionMessages.USERNAME_TAKEN;
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
