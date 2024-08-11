package com.tinqinacademy.authentication.core.services;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.confirmreg.input.ConfirmInput;
import com.tinqinacademy.authentication.api.operations.confirmreg.output.ConfirmOutput;
import com.tinqinacademy.authentication.core.util.JwtTokenUtil;
import com.tinqinacademy.authentication.persistance.entities.User;
import com.tinqinacademy.authentication.persistance.enums.Role;
import com.tinqinacademy.authentication.persistance.repositories.UserRepository;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
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

    private final JwtTokenUtil jwtTokenUtil;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    public String authenticate(String username, String password) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                Optional<User> userOptional = userRepository.findByUsername(username);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    return jwtTokenUtil.generateToken(username, user.getId().toString());
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

}