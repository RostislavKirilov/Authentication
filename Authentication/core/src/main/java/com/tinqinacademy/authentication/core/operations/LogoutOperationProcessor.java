package com.tinqinacademy.authentication.core.operations;

import com.tinqinacademy.authentication.api.operations.logout.input.LogoutInput;
import com.tinqinacademy.authentication.api.operations.logout.output.LogoutOutput;
import com.tinqinacademy.authentication.core.util.JwtTokenProvider;
import com.tinqinacademy.authentication.persistance.entities.BlacklistedToken;
import com.tinqinacademy.authentication.persistance.repositories.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutOperationProcessor {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public LogoutOutput process( LogoutInput logoutInput) {
        String token = logoutInput.getToken();

        // Blacklist the token
        blacklistedTokenRepository.save(new BlacklistedToken(token, jwtTokenProvider.getExpirationDateFromToken(token)));

        return LogoutOutput.builder()
                .message("User has been logged out and token is blacklisted.")
                .build();
    }
}