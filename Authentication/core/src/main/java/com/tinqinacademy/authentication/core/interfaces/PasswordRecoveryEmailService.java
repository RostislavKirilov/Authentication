package com.tinqinacademy.authentication.core.interfaces;

import java.util.concurrent.CompletableFuture;

public interface PasswordRecoveryEmailService {
    CompletableFuture<Void> sendNewPasswordEmail( String to, String newPassword);
}