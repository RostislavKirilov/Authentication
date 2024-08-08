package com.tinqinacademy.authentication.services;

import com.tinqinacademy.authentication.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtTokenUtil jwtTokenUtil;

    public String authenticate(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return null;
        }

        return jwtTokenUtil.generateToken(username);
    }
}