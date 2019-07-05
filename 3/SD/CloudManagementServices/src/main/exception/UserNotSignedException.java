package main.exception;

public class UserNotSignedException extends Exception {
    public UserNotSignedException () {
        super();
    }

    public UserNotSignedException (String message) {
        super(message);
    }
}