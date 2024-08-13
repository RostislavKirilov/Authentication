package com.tinqinacademy.authentication.api.exceptions;

import com.tinqinacademy.authentication.api.messages.ExceptionMessages;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlreadyAdminException extends RuntimeException{
    private final String message = ExceptionMessages.ALREADY_ADMIN;
}