package com.votrebanque.infrastructure.adapters.inbound.rest.controller;

import com.votrebanque.domain.exception.InsufficientFundsException;
import com.votrebanque.domain.exception.InvalidCredentialsException;
import com.votrebanque.application.service.RegisterUserService;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.exception.EmailNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);

    // If the domain says the account does not exist
    @ExceptionHandler(AccountNotFoundException.class)
    public ProblemDetail handleAccountNotFound(AccountNotFoundException ex) {
        // We're sending a generic 404 error to avoid giving too much information.
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Not Found");
    }

    // Job title: The accounts exist, but the balance is insufficient
    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex) {
        // We return a 400 Bad Request with the clean message
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        // Generic client-side message; actual details must be logged server-side
        log.error("Unexpected internal state error", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ProblemDetail handleEmailNotFound(EmailNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}