package main.server.imp.scheduler.exceptions;

public class ResourceNotAvailableException extends Exception {

    public ResourceNotAvailableException() {
        super();
    }

    public ResourceNotAvailableException(String message) {
        super(message);
    }
}