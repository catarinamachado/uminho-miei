package main.server.imp;

import java.time.LocalDateTime;

public class Notification {

    private final String message;

    public Notification(String message) {

        this.message = message + " " + LocalDateTime.now();
    }

    public String message() {
        return this.message;
    }

}