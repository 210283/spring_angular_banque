package com.votrebanque.domain.exception;

// For insufficient balance
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
