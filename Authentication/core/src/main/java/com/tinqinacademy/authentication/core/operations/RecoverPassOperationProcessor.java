package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.base.BaseOperation;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.messages.ExceptionMessages;
import com.tinqinacademy.authentication.api.operations.recoverpass.input.RecoverPassInput;
import com.tinqinacademy.authentication.api.operations.recoverpass.operation.RecoverPassOperation;
import com.tinqinacademy.authentication.api.operations.recoverpass.output.RecoverPassOutput;
import com.tinqinacademy.authentication.core.services.EmailService;
import com.tinqinacademy.authentication.persistance.entities.User;
import com.tinqinacademy.authentication.persistance.repositories.UserRepository;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecoverPassOperationProcessor extends BaseOperation implements RecoverPassOperation {

    private final UserRepository userRepository;
    private final EmailService emailService;

    protected RecoverPassOperationProcessor( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, UserRepository userRepository, EmailService emailService ) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public Either<Errors, RecoverPassOutput> process(RecoverPassInput input) {
        Optional<User> userOptional = userRepository.findByEmail(input.getEmail());
        if (userOptional.isEmpty()) {
            return Either.left(new Errors(ExceptionMessages.EMAIL_NOT_FOUND));
        }

        User user = userOptional.get();

        String newPassword = generateRandomPassword();

        user.setPassword(newPassword);
        userRepository.save(user);

        emailService.sendNewPasswordEmail(user.getEmail(), newPassword);

        return Either.right(RecoverPassOutput.builder().message("Recovery email sent").build());
    }

    private String generateRandomPassword() {
        return "NewRandomPassword123";
    }
}