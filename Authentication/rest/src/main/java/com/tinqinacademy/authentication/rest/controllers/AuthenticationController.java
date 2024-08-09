package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.login.output.LoginOutput;
import com.tinqinacademy.authentication.api.operations.register.input.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.output.RegisterOutput;
import com.tinqinacademy.authentication.api.operations.login.input.LoginInput;
import com.tinqinacademy.authentication.core.operations.LoginOperationProcessor;
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
}


