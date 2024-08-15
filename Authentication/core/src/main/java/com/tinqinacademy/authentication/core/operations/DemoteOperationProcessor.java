package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.base.BaseOperation;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exceptions.DemoteUserException;
import com.tinqinacademy.authentication.api.operations.demote.input.DemoteInput;
import com.tinqinacademy.authentication.api.operations.demote.operation.DemoteOperation;
import com.tinqinacademy.authentication.api.operations.demote.output.DemoteOutput;
import com.tinqinacademy.authentication.core.util.JwtTokenProvider;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import org.springframework.core.convert.ConversionService;
import com.tinqinacademy.authentication.persistance.entities.User;
import com.tinqinacademy.authentication.persistance.enums.Role;
import com.tinqinacademy.authentication.persistance.repositories.UserRepository;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
@Slf4j
public class DemoteOperationProcessor extends BaseOperation implements DemoteOperation {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    protected DemoteOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Either<Errors, DemoteOutput> process(DemoteInput input) {
        return Try.of(() -> {
                    validateInput(input);
                    validateAdminRole(); // Проверка дали е администратор
                    validateFirstAdminRole(); // Проверка дали администраторa е първият администратор
                    demoteUser(input);
                    return DemoteOutput.builder()
                            .message("User demoted to USER successfully.")
                            .build();
                })
                .toEither()
                .mapLeft(this::mapExceptionToErrors);
    }

    private void validateInput(DemoteInput input) {
        if (input.getUserId() == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }

        Optional<User> userOptional = userRepository.findById(UUID.fromString(input.getUserId()));
        if (userOptional.isEmpty() || userOptional.get().getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("User not found or not an ADMIN.");
        }
    }

    private void validateAdminRole() {
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        String role = jwtTokenProvider.getRoleFromToken(token);
        if (!Role.ADMIN.name().equals(role)) {
            throw new IllegalArgumentException("Only admins can demote users.");
        }
    }

    private void validateFirstAdminRole() {
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        String role = jwtTokenProvider.getRoleFromToken(token);

        if (!Role.ADMIN.name().equals(role)) {
            throw new IllegalArgumentException("Only the first admin can demote other admins.");
        }

        // Check if there is more than one admin
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        if (admins.size() > 1) {
            User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found."));

            User firstAdmin = admins.get(0);
            if (!firstAdmin.getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Only the first admin can demote other admins.");
            }
        }
    }

    private void demoteUser(DemoteInput input) {
        Optional<User> userOptional = userRepository.findById(UUID.fromString(input.getUserId()));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getRole() == Role.USER) {
                throw new DemoteUserException();  // Хвърляне на грешка, ако потребителят вече е USER
            }
            user.setRole(Role.USER);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    private Errors mapExceptionToErrors(Throwable throwable) {
        if (throwable instanceof IllegalArgumentException) {
            return createError("Invalid input: " + throwable.getMessage());
        } else if (throwable instanceof DemoteUserException) {
            return createError(throwable.getMessage());
        } else {
            log.error("Unexpected error during demotion", throwable);
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