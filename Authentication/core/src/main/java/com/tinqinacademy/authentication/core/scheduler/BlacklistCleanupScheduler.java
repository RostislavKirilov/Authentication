package com.tinqinacademy.authentication.core.scheduler;

import com.tinqinacademy.authentication.persistance.repositories.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class BlacklistCleanupScheduler {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Scheduled(cron = "0 0 */12 * * *")
    public void cleanUpExpiredTokens() {
        Date now = new Date();
        blacklistedTokenRepository.findAllByExpirationDateBefore(now)
                .forEach(token -> blacklistedTokenRepository.delete(token));
    }
}