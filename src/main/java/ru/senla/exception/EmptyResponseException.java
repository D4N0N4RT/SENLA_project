package ru.senla.exception;

public class EmptyResponseException extends Exception {
    public EmptyResponseException(String message) {
        super(message);
    }
}
