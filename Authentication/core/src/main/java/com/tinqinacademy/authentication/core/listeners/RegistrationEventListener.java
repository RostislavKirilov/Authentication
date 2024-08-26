package com.tinqinacademy.authentication.core.listeners;

import com.tinqinacademy.authentication.core.events.RegistrationEvent;
import com.tinqinacademy.authentication.core.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class RegistrationEventListener implements ApplicationListener<RegistrationEvent> {

    private final EmailService emailService;

    @Autowired
    public RegistrationEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @Override
    public void onApplicationEvent(RegistrationEvent event) {
        emailService.sendRegistrationEmail(event.getTo(), event.getConfirmationCode());
    }
}