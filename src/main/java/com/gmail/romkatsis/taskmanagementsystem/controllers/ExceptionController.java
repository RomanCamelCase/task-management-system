package com.gmail.romkatsis.taskmanagementsystem.controllers;

import com.gmail.romkatsis.taskmanagementsystem.dtos.responses.PlainErrorResponse;
import com.gmail.romkatsis.taskmanagementsystem.dtos.responses.ValidationErrorResponse;
import com.gmail.romkatsis.taskmanagementsystem.exceptions.EmailAlreadyRegisteredException;
import com.gmail.romkatsis.taskmanagementsystem.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public PlainErrorResponse handleAuthenticationError(AuthenticationException exception,
                                                        HttpServletRequest request) {
        return new PlainErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                request.getServletPath(),
                exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public PlainErrorResponse handleAcceptPendingException(HttpServletRequest request) {
        return new PlainErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                request.getServletPath(),
                "You do not have sufficient rights to submit this request");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationError(MethodArgumentNotValidException exception,
                                                         HttpServletRequest request) {

        Map<String, List<String>> errors = exception.getFieldErrors()
                .stream().collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));

        return new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                request.getServletPath(),
                "Form validation error. For details, review the validationErrors field",
                errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public PlainErrorResponse handleMessageNotReadableError(HttpServletRequest request) {
        return new PlainErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                request.getServletPath(),
                "Cannot read request content");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public PlainErrorResponse handleResourceNotFoundError(RuntimeException exception,
                                                          HttpServletRequest request) {
        return new PlainErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                request.getServletPath(),
                exception.getMessage());
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public PlainErrorResponse handleEmailAlreadyRegistered(RuntimeException exception,
                                                           HttpServletRequest request) {
        return new PlainErrorResponse(
                HttpStatus.CONFLICT.value(),
                request.getServletPath(),
                exception.getMessage());
    }
}
