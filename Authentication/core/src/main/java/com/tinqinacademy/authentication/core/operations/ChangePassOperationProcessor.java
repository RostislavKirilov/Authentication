package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.base.BaseOperation;
import com.tinqinacademy.authentication.api.errors.ErrorMapper;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.messages.ExceptionMessages;
import com.tinqinacademy.authentication.api.operations.changepassword.input.ChangePassInput;
import com.tinqinacademy.authentication.api.operations.changepassword.operation.ChangePassOperation;
import com.tinqinacademy.authentication.api.operations.changepassword.output.ChangePassOutput;
import com.tinqinacademy.authentication.persistance.entities.User;
import com.tinqinacademy.authentication.persistance.repositories.UserRepository;
import io.vavr.control.Either;
import jakarta.validation.Validator;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ChangePassOperationProcessor extends BaseOperation implements ChangePassOperation {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    protected ChangePassOperationProcessor(Validator validator, ConversionService conversionService, ErrorMapper errorMapper, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<Errors, ChangePassOutput> process(ChangePassInput input) {
        if (input.getOldPassword() == null || input.getNewPassword() == null || input.getEmail() == null) {
            return Either.left(new Errors(ExceptionMessages.INVALID_DATA_INPUT));
        }

        User user = userRepository.findByEmail(input.getEmail()).orElse(null);
        if (user == null) {
            return Either.left(new Errors(ExceptionMessages.EMAIL_NOT_FOUND));
        }

        if (!passwordEncoder.matches(input.getOldPassword(), user.getPassword())) {
            return Either.left(new Errors(ExceptionMessages.WRONG_PASSWORD_USERNAME));
        }
        user.setPassword(passwordEncoder.encode(input.getNewPassword()));
        userRepository.save(user);

        return Either.right(ChangePassOutput.builder().message("Password changed successfully").build());
    }
}
