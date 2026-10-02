package com.votrebanque.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class DemoSession extends AbstractEntity<String> {

    private final String id;
    private final String username;
    private final List<String> accountNumbers;
    private final Instant createdAt;
    private final Instant expiresAt;

    private DemoSession(String id, String username, List<String> accountNumbers, Instant createdAt, Instant expiresAt) {
        this.id = Objects.requireNonNull(id, "The demo session id cannot be null.");
        this.username = Objects.requireNonNull(username, "The demo session username cannot be null.");
        this.accountNumbers = List.copyOf(Objects.requireNonNull(accountNumbers, "The account numbers cannot be null."));
        this.createdAt = Objects.requireNonNull(createdAt, "The creation date cannot be null.");
        this.expiresAt = Objects.requireNonNull(expiresAt, "The expiry date cannot be null.");
        if (accountNumbers.isEmpty()) {
            throw new IllegalArgumentException("A demo session must have at least one account.");
        }
    }

    public static DemoSession create(String id, String username, List<String> accountNumbers, Instant createdAt, Instant expiresAt) {
        return new DemoSession(id, username, accountNumbers, createdAt, expiresAt);
    }

    public static DemoSession reconstruct(String id, String username, List<String> accountNumbers, Instant createdAt, Instant expiresAt) {
        return new DemoSession(id, username, accountNumbers, createdAt, expiresAt);
    }

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public String getUsername() {
        return username;
    }

    public List<String> getAccountNumbers() {
        return accountNumbers;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String id() {
        return id;
    }
}
