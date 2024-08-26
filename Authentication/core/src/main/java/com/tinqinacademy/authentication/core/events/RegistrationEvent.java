package com.tinqinacademy.authentication.core.events;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class RegistrationEvent extends ApplicationEvent {
    private final String to;
    private final String confirmationCode;

    public RegistrationEvent(Object source, String to, String confirmationCode) {
        super(source);
        this.to = to;
        this.confirmationCode = confirmationCode;
    }

}
