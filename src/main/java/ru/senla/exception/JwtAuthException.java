package ru.senla.exception;

import javax.naming.AuthenticationException;

public class JwtAuthException extends AuthenticationException {
    public JwtAuthException(String explanation) {
        super(explanation);
    }
}