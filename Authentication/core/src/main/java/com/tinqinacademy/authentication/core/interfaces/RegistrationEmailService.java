package com.tinqinacademy.authentication.core.interfaces;

import java.util.concurrent.CompletableFuture;

public interface RegistrationEmailService {
    CompletableFuture<Void> sendRegistrationEmail( String to, String confirmationCode);
}