package com.votrebanque.domain.validator;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {

    public void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password too long (72 bytes max)");
        }
        if (rawPassword.length() < 10) {
            throw new IllegalArgumentException("The password must contain at least 10 characters");
        }
        if (!rawPassword.matches(".*[A-Z].*") || !rawPassword.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("The password must contain an uppercase letter and a digit");
        }
    }
}
