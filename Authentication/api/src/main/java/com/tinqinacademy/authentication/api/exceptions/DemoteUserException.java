package com.tinqinacademy.authentication.api.exceptions;

import com.tinqinacademy.authentication.api.messages.ExceptionMessages;
import lombok.Getter;

@Getter
public class DemoteUserException extends RuntimeException {
    private final String message;

    public DemoteUserException() {
        this.message = ExceptionMessages.DEMOTE_BELOW;
    }

    public DemoteUserException(String customMessage) {
        this.message = customMessage;
    }
}