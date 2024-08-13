package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.changepassword.input.ChangePassInput;
import com.tinqinacademy.authentication.api.operations.changepassword.output.ChangePassOutput;
import com.tinqinacademy.authentication.api.operations.confirmreg.input.ConfirmInput;
import com.tinqinacademy.authentication.api.operations.confirmreg.output.ConfirmOutput;
import com.tinqinacademy.authentication.api.operations.demote.input.DemoteInput;
import com.tinqinacademy.authentication.api.operations.demote.output.DemoteOutput;
import com.tinqinacademy.authentication.api.operations.login.output.LoginOutput;
import com.tinqinacademy.authentication.api.operations.promote.input.PromoteInput;
import com.tinqinacademy.authentication.api.operations.promote.output.PromoteOutput;
import com.tinqinacademy.authentication.api.operations.recoverpass.input.RecoverPassInput;
import com.tinqinacademy.authentication.api.operations.recoverpass.output.RecoverPassOutput;
import com.tinqinacademy.authentication.api.operations.register.input.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.output.RegisterOutput;
import com.tinqinacademy.authentication.api.operations.login.input.LoginInput;
import com.tinqinacademy.authentication.core.operations.*;
import com.tinqinacademy.authentication.core.services.AuthenticationService;
import com.tinqinacademy.authentication.core.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@Validated
@RestController
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LoginOperationProcessor loginOperationProcessor;
    private final PromoteOperationProcessor promoteOperationProcessor;
    private final DemoteOperationProcessor demoteOperationProcessor;
    private final JwtTokenProvider jwtTokenProvider;
    private final RegisterOperationProcessor registerOperationProcessor;
    private final RecoverPassOperationProcessor recoverPassOperationProcessor;
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

        Either<Errors, RegisterOutput> result = registerOperationProcessor.process(registerInput);

        return result.fold(
                errors -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null),
                ResponseEntity::ok
        );
    }

    @PostMapping("/auth/confirm")
    @Operation(summary = "Confirm registration with a code")
    public ResponseEntity<String> confirmRegistration(@RequestBody @Valid ConfirmInput confirmInput) {
        log.info("Attempting to confirm registration with code: {}", confirmInput.getCCode());

        Either<Errors, ConfirmOutput> result = authenticationService.confirmRegistration(confirmInput);

        return result.fold(
                errors -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getMessage()),
                confirmOutput -> ResponseEntity.ok(confirmOutput.getMessage())
        );
    }

    @PostMapping("/auth/promote")
    @Operation(summary = "Promote a user to ADMIN")
    public ResponseEntity<?> promoteUser(@RequestBody @Valid PromoteInput promoteInput) {
        log.info("Attempting to promote user with ID: {}", promoteInput.getUserId());

        Either<Errors, PromoteOutput> result = promoteOperationProcessor.process(promoteInput);

        return result.fold(
                errors -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getMessage()),
                promoteOutput -> ResponseEntity.ok(promoteOutput.getMessage())
        );
    }

    @PostMapping("/auth/demote")
    @Operation(summary = "Demote an admin to USER")
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Errors.class)))
    public ResponseEntity<?> demoteUser(@RequestBody @Valid DemoteInput demoteInput) {
        log.info("Attempting to demote user with ID: {}", demoteInput.getUserId());

        Either<Errors, DemoteOutput> result = demoteOperationProcessor.process(demoteInput);

        return result.fold(
                errors -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors),  // Връщане на целия Errors обект
                demoteOutput -> ResponseEntity.ok(demoteOutput.getMessage())
        );
    }

    @PostMapping("/recover-password")
    @Operation(summary = "Recover password by email")
    public ResponseEntity<RecoverPassOutput> recoverPassword(@RequestBody @Valid RecoverPassInput recoverPassInput) {
        Either<Errors, RecoverPassOutput> result = recoverPassOperationProcessor.process(recoverPassInput);

        return result.fold(
                errors -> ResponseEntity.status(HttpStatus.OK).body(RecoverPassOutput.builder().message(errors.getMessage()).build()),
                ResponseEntity::ok
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ChangePassOutput> changePassword(@RequestHeader("Authorization") String token, @RequestBody @Valid ChangePassInput changePassInput) {
        String jwtToken = token.replace("Bearer ", "");

        if (!jwtTokenProvider.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ChangePassOutput.builder().message("Invalid token").build());
        }
        Either<Errors, ChangePassOutput> result = authenticationService.changePassword(changePassInput, jwtToken);

        return result.fold(
                errors -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ChangePassOutput.builder().message(errors.getMessage()).build()),
                ResponseEntity::ok
        );
    }
}


