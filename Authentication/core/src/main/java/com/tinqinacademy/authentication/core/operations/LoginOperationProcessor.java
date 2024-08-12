package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.base.BaseOperation;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.login.input.LoginInput;
import com.tinqinacademy.authentication.api.operations.login.operation.LoginOperation;
import com.tinqinacademy.authentication.api.operations.login.output.LoginOutput;
import com.tinqinacademy.authentication.core.services.CustomUserDetails;
import com.tinqinacademy.authentication.core.util.JwtTokenProvider;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LoginOperationProcessor extends BaseOperation implements LoginOperation {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    protected LoginOperationProcessor( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider ) {
        super(validator, conversionService, errorMapper);
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    public Either<Errors, LoginOutput> process(LoginInput input) {
        return Try.of(() -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(input.getUsername());
                    if (!(userDetails instanceof CustomUserDetails)) {
                        throw new AuthenticationException("Invalid UserDetails instance") {};
                    }

                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

                    if (!passwordEncoder.matches(input.getPassword(), customUserDetails.getPassword())) {
                        throw new AuthenticationException("Invalid password") {};
                    }

                    String userId = customUserDetails.getUserId().toString();
                    String jwtToken = generateJwtToken(customUserDetails.getUsername(), userId);
                    return LoginOutput.builder().jwtToken(jwtToken).build();
                })
                .toEither()
                .mapLeft(this::mapExceptionToErrors);
    }

    private String generateJwtToken(String username, String userId) {
        return jwtTokenProvider.generateToken(username, userId);
    }

    private Errors mapExceptionToErrors(Throwable throwable) {
        if (throwable instanceof UsernameNotFoundException) {
            return createError("Username not found");
        } else if (throwable instanceof AuthenticationException) {
            return createError("Authentication failed");
        } else {
            log.error("Unexpected error during login", throwable);
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