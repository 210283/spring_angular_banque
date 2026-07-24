package com.votrebanque.domain.exception;

// For insufficient balance
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}