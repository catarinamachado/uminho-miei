package main.server.imp.scheduler.exceptions;

public class RequestNotFoundException extends Exception {

    public RequestNotFoundException() {
        super();
    }

    public RequestNotFoundException(String message) {
        super(message);
    }
}