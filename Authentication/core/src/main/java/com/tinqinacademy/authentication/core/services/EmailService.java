package com.tinqinacademy.authentication.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public CompletableFuture<Void> sendRegistrationEmail( String to, String confirmationCode) {
        String subject = "Confirm your registration";
        String text = "Thank you for registering. Please use the following code to confirm your registration: " + confirmationCode;
        return sendEmail(to, subject, text);
    }

    @Async
    public CompletableFuture<Void> sendNewPasswordEmail(String to, String newPassword) {
        String subject = "Password Recovery";
        String text = "Your new password is: " + newPassword;
        return sendEmail(to, subject, text);
    }

    private CompletableFuture<Void> sendEmail(String to, String subject, String text) {
        return CompletableFuture.runAsync(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        });
    }
}