package com.tinqinacademy.authentication.core.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationEmail(String to, String confirmationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hotel@tinqin.com");
        message.setTo(to);
        message.setSubject("Registration Confirmation");
        message.setText("Thank you for registering. Please use the following code to confirm your email: " + confirmationCode);

        mailSender.send(message);
    }

    public void sendNewPasswordEmail(String to, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hotel@tinqin.com");
        message.setTo(to);
        message.setSubject("Password Recovery");
        message.setText("Your new password is: " + newPassword);

        mailSender.send(message);
    }
}
