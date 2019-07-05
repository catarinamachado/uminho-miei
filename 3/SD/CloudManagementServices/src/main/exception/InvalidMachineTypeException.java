package main.exception;

public class InvalidMachineTypeException extends Exception {
    public InvalidMachineTypeException() {
        super();
    }

    public InvalidMachineTypeException(String message) {
        super(message);
    }
}
