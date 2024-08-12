package com.tinqinacademy.authentication.core.services;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.changepassword.input.ChangePassInput;
import com.tinqinacademy.authentication.api.operations.changepassword.output.ChangePassOutput;
import com.tinqinacademy.authentication.api.operations.confirmreg.input.ConfirmInput;
import com.tinqinacademy.authentication.api.operations.confirmreg.output.ConfirmOutput;
import com.tinqinacademy.authentication.api.operations.recoverpass.input.RecoverPassInput;
import com.tinqinacademy.authentication.api.operations.recoverpass.output.RecoverPassOutput;
import com.tinqinacademy.authentication.core.util.JwtTokenProvider;
import com.tinqinacademy.authentication.persistance.entities.User;
import com.tinqinacademy.authentication.persistance.enums.Role;
import com.tinqinacademy.authentication.persistance.repositories.UserRepository;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomUserDetailsService userDetailsService;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final EmailService emailService;

    public String authenticate(String username, String password) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                Optional<User> userOptional = userRepository.findByUsername(username);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    return jwtTokenProvider.generateToken(username, user.getId().toString());
                }
            }
        } catch (UsernameNotFoundException e) {
        }
        return null;
    }

    public UUID registerUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public void updateUserWithConfirmationCode(UUID userId, String confirmationCode, long expiryTime) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setConfirmationCode(confirmationCode);
            user.setConfirmationExpiry(expiryTime);
            userRepository.save(user);
        }
    }

    public Either<Errors, ConfirmOutput> confirmRegistration( ConfirmInput confirmInput) {
        Optional<User> userOptional = userRepository.findByConfirmationCode(confirmInput.getCCode());

        if (userOptional.isEmpty()) {
            return Either.left(Errors.builder().message("Invalid confirmation code").build());
        }

        User user = userOptional.get();
        if (System.currentTimeMillis() > user.getConfirmationExpiry()) {
            return Either.left(Errors.builder().message("Confirmation code expired").build());
        }

        user.setConfirmed(true);
        user.setConfirmationCode(null);
        user.setConfirmationExpiry(0);
        userRepository.save(user);

        return Either.right(ConfirmOutput.builder().message("Email confirmed successfully").build());
    }

    public Either<Errors, RecoverPassOutput> recoverPassword( RecoverPassInput recoverPassInput) {
        Optional<User> userOptional = userRepository.findByEmail(recoverPassInput.getEmail());

        if (userOptional.isEmpty()) {
            return Either.right(RecoverPassOutput.builder()
                    .message("If an account with this email exists, a new password has been sent.")
                    .build());
        }

        User user = userOptional.get();

        String newPassword = PasswordGenerator.generateRandomPassword();

        // Криптирам
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        // Изпращам
        emailService.sendNewPasswordEmail(user.getEmail(), newPassword);

        return Either.right(RecoverPassOutput.builder()
                .message("If an account with this email exists, a new password has been sent.")
                .build());
    }

    public Either<Errors, ChangePassOutput> changePassword(ChangePassInput changePassInput, String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return Either.left(new Errors("Invalid token."));
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        String username = authentication.getName();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return Either.left(new Errors("User not found."));
        }

        User user = userOptional.get();

        if (!user.getEmail().equals(changePassInput.getEmail())) {
            return Either.left(new Errors("Email does not match the logged in user."));
        }

        if (!passwordEncoder.matches(changePassInput.getOldPassword(), user.getPassword())) {
            return Either.left(new Errors("Old password is incorrect."));
        }

        user.setPassword(passwordEncoder.encode(changePassInput.getNewPassword()));
        userRepository.save(user);

        return Either.right(ChangePassOutput.builder()
                .message("Password successfully changed.")
                .build());
    }

}