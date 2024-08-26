package com.tinqinacademy.authentication.core.listeners;

import com.tinqinacademy.authentication.core.events.PasswordRecoveryEvent;
import com.tinqinacademy.authentication.core.services.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PasswordRecoveryEventListener implements ApplicationListener<PasswordRecoveryEvent> {

    private final EmailService emailService;

    public PasswordRecoveryEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @Override
    public void onApplicationEvent(PasswordRecoveryEvent event) {
        emailService.sendNewPasswordEmail(event.getTo(), event.getNewPassword());
    }
}