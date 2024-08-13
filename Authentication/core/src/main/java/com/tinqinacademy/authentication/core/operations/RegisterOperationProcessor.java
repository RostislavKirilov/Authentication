package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.base.BaseOperation;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exceptions.EmailAlreadyExistsException;
import com.tinqinacademy.authentication.api.exceptions.UsernameAlreadyExistsException;
import com.tinqinacademy.authentication.api.operations.register.operation.RegisterOperation;
import com.tinqinacademy.authentication.api.operations.register.input.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.output.RegisterOutput;
import com.tinqinacademy.authentication.core.services.EmailService;
import com.tinqinacademy.authentication.persistance.entities.User;
import com.tinqinacademy.authentication.persistance.enums.Role;
import com.tinqinacademy.authentication.persistance.repositories.UserRepository;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class RegisterOperationProcessor extends BaseOperation implements RegisterOperation {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public RegisterOperationProcessor( Validator validator,
                                       ConversionService conversionService,
                                       ErrorMapper errorMapper,
                                       UserRepository userRepository,
                                       BCryptPasswordEncoder passwordEncoder,
                                       EmailService emailService) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public Either<Errors, RegisterOutput> process ( RegisterInput input ) {
        try {
            if (userRepository.existsByUsername(input.getUsername())) {
                throw new UsernameAlreadyExistsException("Username already exists: " + input.getUsername());
            }

            if (userRepository.existsByEmail(input.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + input.getEmail());
            }

            User user = new User();
            user.setUsername(input.getUsername());
            user.setEmail(input.getEmail());
            user.setPassword(passwordEncoder.encode(input.getPassword()));
            user.setRole(Role.USER);

            User savedUser = userRepository.save(user);

            String confirmationCode = UUID.randomUUID().toString();
            long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);
            savedUser.setConfirmationCode(confirmationCode);
            savedUser.setConfirmationExpiry(expiryTime);
            userRepository.save(savedUser);

            emailService.sendRegistrationEmail(input.getEmail(), confirmationCode);

            RegisterOutput registerOutput = RegisterOutput.builder()
                    .userId(savedUser.getId().toString())
                    .build();
            return Either.right(registerOutput);

        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            log.error("Error during registration", e);
            return Either.left(Errors.builder().message(e.getMessage()).build());
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return Either.left(Errors.builder().message("Unexpected error during registration").build());
        }
    }
}
