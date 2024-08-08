package com.tinqinacademy.authentication.core.services;

import com.tinqinacademy.authentication.api.util.JwtTokenUtil;
import com.tinqinacademy.authentication.persistance.entities.User;
import com.tinqinacademy.authentication.persistance.enums.Role;
import com.tinqinacademy.authentication.persistance.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtTokenUtil jwtTokenUtil;
    private UserRepository userRepository;
    public String authenticate(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return null;
        }

        return jwtTokenUtil.generateToken(username);
    }

    @Transactional
    public String registerUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return null;
        }

        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return jwtTokenUtil.generateToken(username);
    }
}