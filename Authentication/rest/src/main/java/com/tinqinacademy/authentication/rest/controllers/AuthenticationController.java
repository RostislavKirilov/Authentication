package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.demote.input.DemoteInput;
import com.tinqinacademy.authentication.api.operations.demote.output.DemoteOutput;
import com.tinqinacademy.authentication.api.operations.login.output.LoginOutput;
import com.tinqinacademy.authentication.api.operations.promote.input.PromoteInput;
import com.tinqinacademy.authentication.api.operations.promote.output.PromoteOutput;
import com.tinqinacademy.authentication.api.operations.register.input.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.output.RegisterOutput;
import com.tinqinacademy.authentication.api.operations.login.input.LoginInput;
import com.tinqinacademy.authentication.core.operations.DemoteOperationProcessor;
import com.tinqinacademy.authentication.core.operations.LoginOperationProcessor;
import com.tinqinacademy.authentication.core.operations.PromoteOperationProcessor;
import com.tinqinacademy.authentication.core.services.AuthenticationService;
import com.tinqinacademy.authentication.core.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final LoginOperationProcessor loginOperationProcessor;
    private final PromoteOperationProcessor promoteOperationProcessor;
    private final DemoteOperationProcessor demoteOperationProcessor;
    @PostMapping("/auth/login")
    @Operation(summary = "Log in and get a JWT token")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginInput loginInput) {
        log.info("Attempting to log in with username: {}", loginInput.getUsername());

        Either<Errors, LoginOutput> result = loginOperationProcessor.process(loginInput);

        return result.fold(
                errors -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
                loginOutput -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", "Bearer " + loginOutput.getJwtToken());
                    return ResponseEntity.ok().headers(headers).build();
                }
        );
    }

    @PostMapping("/auth/register")
    @Operation(summary = "Register a new user and save to database")
    public ResponseEntity<RegisterOutput> registerUser(@RequestBody @Valid RegisterInput registerInput) {
        log.info("Attempting to register user with username: {}", registerInput.getUsername());

        UUID userId = authenticationService.registerUser(
                registerInput.getUsername(),
                registerInput.getPassword(),
                registerInput.getEmail()
        );

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        RegisterOutput registerOutput = RegisterOutput.builder()
                .userId(userId.toString())
                .build();

        return ResponseEntity.ok(registerOutput);
    }

    @PostMapping("/auth/promote")
    @Operation(summary = "Promote a user to ADMIN")
    public ResponseEntity<String> promoteUser(@RequestBody @Valid PromoteInput promoteInput) {
        log.info("Attempting to promote user with ID: {}", promoteInput.getUserId());

        Either<Errors, PromoteOutput> result = promoteOperationProcessor.process(promoteInput);

        return result.fold(
                errors -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getMessage()),
                promoteOutput -> ResponseEntity.ok(promoteOutput.getMessage())
        );
    }

    @PostMapping("/auth/demote")
    @Operation(summary = "Demote an admin to USER")
    public ResponseEntity<String> demoteUser(@RequestBody @Valid DemoteInput demoteInput) {
        log.info("Attempting to demote user with ID: {}", demoteInput.getUserId());

        Either<Errors, DemoteOutput> result = demoteOperationProcessor.process(demoteInput);

        return result.fold(
                errors -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getMessage()),
                demoteOutput -> ResponseEntity.ok(demoteOutput.getMessage())
        );
    }
}


