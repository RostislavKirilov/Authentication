package com.tinqinacademy.authentication.core.events;

import lombok.*;
import org.springframework.context.ApplicationEvent;


@Getter
public class PasswordRecoveryEvent extends ApplicationEvent {
    private final String to;
    private final String newPassword;

    public PasswordRecoveryEvent(String to, String newPassword) {
        super(to);
        this.to = to;
        this.newPassword = newPassword;
    }

}