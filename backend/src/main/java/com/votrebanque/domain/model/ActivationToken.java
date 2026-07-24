package com.votrebanque.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ActivationToken extends AbstractEntity<String> {
    private final String id;
    private final String hashedToken;
    private final String username;
    private final Instant expiresAt;
    private boolean used;
    private boolean emailSent;

    private ActivationToken(String id, String hashedToken, String username, Instant expiresAt, boolean used, boolean emailSent) {
        this.id = Objects.requireNonNull(id, "The id cannot be null.");
        this.hashedToken = Objects.requireNonNull(hashedToken, "The token cannot be null.");
        this.username = Objects.requireNonNull(username, "The username cannot be null.");
        this.expiresAt = Objects.requireNonNull(expiresAt, "The expiration date cannot be null.");
        this.used = used;
        this.emailSent = emailSent;
    }

    public static ActivationToken generate(String username, String hashedToken, Duration validity) {
        Objects.requireNonNull(username, "The username cannot be null.");
        Objects.requireNonNull(hashedToken, "The token cannot be null.");
        Objects.requireNonNull(validity, "The validity duration cannot be null.");

        return new ActivationToken(
            UUID.randomUUID().toString(),
            hashedToken,
            username,
            Instant.now().plus(validity),
            false,
            false
        );
    }

    public static ActivationToken reconstruct(String id, String hashedToken, String username,
                                               Instant expiresAt, boolean used, boolean emailSent) {
        return new ActivationToken(id, hashedToken, username, expiresAt, used, emailSent);
    }

    public boolean isValid() {
        return !used && Instant.now().isBefore(expiresAt);
    }

    public boolean attemptActivation(String rawToken, TokenEncoder tokenEncoder) {
        if (!isValid()) {
            return false;
        }

        if (tokenEncoder.matches(rawToken, this.hashedToken)) {
            this.used = true;
            return true;
        }

        return false;
    }

    public String getUsername() {
        return username;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getHashedToken() {
        return hashedToken;
    }

    public boolean isUsed() {
        return used;
    }

    public void markEmailSent() {
    this.emailSent = true;
}

    public boolean isEmailSent() {
        return emailSent;
    }

    @Override
    public String id() {
        return id;
    }

    public interface TokenEncoder {
        boolean matches(CharSequence rawToken, String hashedToken);
    }
}