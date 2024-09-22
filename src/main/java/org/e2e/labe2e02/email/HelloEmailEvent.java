package org.e2e.labe2e02.email;

import org.springframework.context.ApplicationEvent;

public class HelloEmailEvent extends ApplicationEvent {
    private final String email;

    public HelloEmailEvent(String email) {
        super(email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}