package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.base.BaseOperation;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exceptions.InvalidInputException;
import com.tinqinacademy.authentication.api.exceptions.EmailNotFoundException;
import com.tinqinacademy.authentication.api.messages.ExceptionMessages;
import com.tinqinacademy.authentication.api.exceptions.PasswordUsernameException;
import com.tinqinacademy.authentication.api.exceptions.UserNotFoundException;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Map;
import java.util.function.Function;
import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class LoginOperationProcessor extends BaseOperation implements LoginOperation {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private Map<Class<? extends Throwable>, Function<Throwable, Errors>> exceptionMappings;

    protected LoginOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper, UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        super(validator, conversionService, errorMapper);
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostConstruct
    private void initExceptionMappings() {
        exceptionMappings = Map.of(
                UserNotFoundException.class, ex -> createError(ExceptionMessages.USER_NOT_FOUND),
                EmailNotFoundException.class, ex -> createError(ExceptionMessages.EMAIL_NOT_FOUND),
                PasswordUsernameException.class, ex -> createError(ExceptionMessages.WRONG_PASSWORD_USERNAME),
                InvalidInputException.class, ex -> createError(ExceptionMessages.INVALID_DATA_INPUT),
                AuthenticationException.class, ex -> createError("Authentication failed")
        );
    }

    @Override
    public Either<Errors, LoginOutput> process(LoginInput input) {
        return Try.of(() -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(input.getUsername());
                    if (!(userDetails instanceof CustomUserDetails)) {
                        throw new UserNotFoundException();
                    }

                    CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

                    if (!passwordEncoder.matches(input.getPassword(), customUserDetails.getPassword())) {
                        throw new PasswordUsernameException();
                    }

                    String userId = customUserDetails.getUserId().toString();
                    String role = customUserDetails.getAuthorities().stream()
                            .filter(authority -> authority instanceof SimpleGrantedAuthority)
                            .map(authority -> ((SimpleGrantedAuthority) authority).getAuthority())
                            .findFirst()
                            .orElse("USER"); // Можете да зададете стойност по подразбиране или да хвърлите изключение

                    String jwtToken = generateJwtToken(customUserDetails.getUsername(), userId, role);
                    return LoginOutput.builder().jwtToken(jwtToken).build();
                })
                .toEither()
                .mapLeft(this::mapExceptionToErrors);
    }



    private String generateJwtToken(String username, String userId, String role) {
        return jwtTokenProvider.generateToken(username, userId, role);
    }


    private Errors mapExceptionToErrors(Throwable throwable) {
        return exceptionMappings
                .getOrDefault(throwable.getClass(), ex -> {
                    log.error("Unexpected error during login", throwable);
                    return createError("Unexpected error");
                })
                .apply(throwable);
    }

    private Errors createError(String message) {
        return Errors.builder()
                .message(message)
                .errors(List.of())
                .build();
    }
}
