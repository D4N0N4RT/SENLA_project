package ru.senla.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.senla.exception.DuplicateUsernameException;
import ru.senla.exception.EmptyResponseException;
import ru.senla.exception.JwtAuthException;
import ru.senla.exception.PasswordCheckException;
import ru.senla.exception.WrongAuthorityException;
import ru.senla.exception.WrongIdException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { EmptyResponseException.class, WrongIdException.class })
    protected ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(),
                HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { AuthenticationException.class, JwtAuthException.class,
            UsernameNotFoundException.class })
    protected ResponseEntity<Object> handleAuthExceptions(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(),
                HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = { WrongAuthorityException.class, PasswordCheckException.class,
            DuplicateUsernameException.class })
    protected ResponseEntity<Object> handleConflictExceptions(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(),
                HttpStatus.CONFLICT, request);
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, "Ошибка валидации, проверьте введенные данные", new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }
}
