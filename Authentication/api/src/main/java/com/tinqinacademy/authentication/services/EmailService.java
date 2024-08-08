package com.tinqinacademy.authentication.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationEmail(String to, String userId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hotel@tinqin.com");
        message.setTo(to);
        message.setSubject("Registration Confirmation");
        message.setText("Thank you for registering. Your user ID is " + userId);

        mailSender.send(message);
    }
}
