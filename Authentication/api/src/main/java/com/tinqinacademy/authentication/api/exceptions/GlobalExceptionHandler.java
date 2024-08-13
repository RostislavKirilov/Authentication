package com.tinqinacademy.authentication.api.exceptions;

import com.tinqinacademy.authentication.api.errors.Errors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Errors> handleIllegalArgumentException( IllegalArgumentException ex) {
        Errors errors = Errors.builder().message(ex.getMessage()).build();
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Errors> handleGenericException(Exception ex) {
        Errors errors = Errors.builder().message("Unexpected error occurred").build();
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}