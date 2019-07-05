package main.exception;

public class InvalidUserCredentialsException extends Exception {
    public InvalidUserCredentialsException() {
        super();
    }

    public InvalidUserCredentialsException(String message) {
        super(message);
    }
}
