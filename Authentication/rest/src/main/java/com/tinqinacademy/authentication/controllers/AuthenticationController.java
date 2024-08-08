package com.tinqinacademy.authentication.controllers;

import com.tinqinacademy.authentication.operations.register.input.RegisterInput;
import com.tinqinacademy.authentication.operations.register.output.RegisterOutput;
import com.tinqinacademy.authentication.services.AuthenticationService;
import com.tinqinacademy.authentication.operations.login.input.LoginInput;
import com.tinqinacademy.authentication.operations.login.output.LoginOutput;
import com.tinqinacademy.authentication.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RestController
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    @PostMapping("/auth/login")
    @Operation(summary = "Log in and get a JWT token")
    public ResponseEntity<?> login( @RequestBody @Valid LoginInput loginInput) {
        log.info("Attempting to log in with username: {}", loginInput.getUsername());

        String jwtToken = authenticationService.authenticate(loginInput.getUsername(), loginInput.getPassword());

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        LoginOutput loginOutput = LoginOutput.builder()
                .jwtToken(jwtToken)
                .build();

        return ResponseEntity.ok(loginOutput);
    }

    @PostMapping("/auth/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<RegisterOutput> register(@RequestBody @Valid RegisterInput registerInput) {
        log.info("Attempting to register user with username: {}", registerInput.getUsername());


        String userId = "generatedUserId";
        emailService.sendRegistrationEmail(registerInput.getEmail(), userId);

        RegisterOutput registerOutput = RegisterOutput.builder()
                .userId(userId)
                .build();

        return ResponseEntity.ok(registerOutput);
    }
}
