package com.votrebanque.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DemoSession extends AbstractEntity<String> {

    private final String id;
    private final List<String> usernames;
    private final List<String> accountNumbers;
    private final Instant createdAt;
    private final Instant expiresAt;

    private DemoSession(String id, List<String> usernames, List<String> accountNumbers, Instant createdAt, Instant expiresAt) {
        this.id = Objects.requireNonNull(id, "The demo session id cannot be null.");
        this.usernames = List.copyOf(Objects.requireNonNull(usernames, "The usernames cannot be null."));
        this.accountNumbers = List.copyOf(Objects.requireNonNull(accountNumbers, "The account numbers cannot be null."));
        this.createdAt = Objects.requireNonNull(createdAt, "The creation date cannot be null.");
        this.expiresAt = Objects.requireNonNull(expiresAt, "The expiry date cannot be null.");
        if (accountNumbers.isEmpty()) {
            throw new IllegalArgumentException("A demo session must have at least one account.");
        }
        if (usernames.isEmpty()) {
            throw new IllegalArgumentException("A demo session must have at least one username.");
        }
    }

    public static DemoSession create(String id, List<String> usernames, List<String> accountNumbers, Instant createdAt, Instant expiresAt) {
        return new DemoSession(id, usernames, accountNumbers, createdAt, expiresAt);
    }

    public static DemoSession reconstruct(String id, List<String> usernames, List<String> accountNumbers, Instant createdAt, Instant expiresAt) {
        return new DemoSession(id, usernames, accountNumbers, createdAt, expiresAt);
    }

    // Called when the demo admin identity opens an extra account, so it gets cleaned up with the rest of the session.
    public DemoSession withAdditionalAccount(String accountNumber, String username) {
        List<String> updatedAccountNumbers = new ArrayList<>(this.accountNumbers);
        updatedAccountNumbers.add(accountNumber);

        List<String> updatedUsernames = new ArrayList<>(this.usernames);
        if (username != null && !updatedUsernames.contains(username)) {
            updatedUsernames.add(username);
        }

        return new DemoSession(this.id, updatedUsernames, updatedAccountNumbers, this.createdAt, this.expiresAt);
    }

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public List<String> getUsernames() {
        return usernames;
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
