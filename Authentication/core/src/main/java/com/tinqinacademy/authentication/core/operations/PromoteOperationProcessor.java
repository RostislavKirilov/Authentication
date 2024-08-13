package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.base.BaseOperation;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exceptions.AlreadyAdminException;
import com.tinqinacademy.authentication.api.messages.ExceptionMessages;
import com.tinqinacademy.authentication.api.operations.promote.input.PromoteInput;
import com.tinqinacademy.authentication.api.operations.promote.operation.PromoteOperation;
import com.tinqinacademy.authentication.api.operations.promote.output.PromoteOutput;
import com.tinqinacademy.authentication.persistance.entities.User;
import com.tinqinacademy.authentication.persistance.enums.Role;
import com.tinqinacademy.authentication.persistance.repositories.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;


import java.util.Optional;

@Service
@Slf4j
public class PromoteOperationProcessor extends BaseOperation implements PromoteOperation {

    private final UserRepository userRepository;

    protected PromoteOperationProcessor (Validator validator, ConversionService conversionService, ErrorMapper errorMapper, UserRepository userRepository) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    public Either<Errors, PromoteOutput> process(PromoteInput input) {
        return Try.of(() -> {
                    validateInput(input);
                    promoteUser(input);
                    return PromoteOutput.builder()
                            .message("User promoted to ADMIN successfully.")
                            .build();
                })
                .toEither()
                .mapLeft(this::mapExceptionToErrors);
    }

    private void validateInput(PromoteInput input) {
        if (input.getUserId() == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }

        if (!userRepository.existsById(input.getUserId())) {
            throw new IllegalArgumentException("User not found.");
        }
    }

    private void promoteUser(PromoteInput input) {
        Optional<User> userOptional = userRepository.findById(input.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getRole() == Role.ADMIN) {
                throw new AlreadyAdminException();
            }
            user.setRole(Role.ADMIN);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    private Errors mapExceptionToErrors(Throwable throwable) {
        log.error("Exception occurred: ", throwable);

        if (throwable instanceof IllegalArgumentException) {
            return createError("Invalid input: " + throwable.getMessage());
        } else if (throwable instanceof AlreadyAdminException) {
            return createError(ExceptionMessages.ALREADY_ADMIN);
        } else {
            log.error("Unexpected error during promotion", throwable);
            return createError("Unexpected error");
        }
    }

    private Errors createError(String message) {
        return Errors.builder()
                .message(message)
                .errors(List.of())
                .build();
    }
}