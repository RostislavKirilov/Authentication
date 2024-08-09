package com.tinqinacademy.authentication.api.errors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ErrorMapper {

    public <T extends Throwable> ErrorOutput map(T throwable, HttpStatus httpStatus) {
        return ErrorOutput.builder()
                .errors(List.of(Errors.builder()
                        .message(throwable.getMessage())
                        .build()))
                .status(httpStatus)
                .build();
    }

    public ErrorOutput mapErrors( List<Errors> errors, HttpStatus httpStatus) {
        return ErrorOutput.builder()
                .errors(errors)
                .status(httpStatus)
                .build();
    }
}
