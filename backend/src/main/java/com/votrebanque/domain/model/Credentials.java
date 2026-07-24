package com.votrebanque.domain.model;

import java.time.Instant;
import java.util.Objects;

public class Credentials extends AbstractEntity<String> {
    private final String username;
    private final AccountId accountId;
    private String hashedPassword;
    private boolean mustChangePassword;
    private int failedLoginAttempts;
    private Instant lockedUntil;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_SECONDS = 900;

    private Credentials(String username, AccountId accountId, String hashedPassword, boolean mustChangePassword,
                         int failedLoginAttempts, Instant lockedUntil) {
        this.username = Objects.requireNonNull(username, "The username cannot be null.");
        this.accountId = Objects.requireNonNull(accountId, "The account id cannot be null.");
        this.hashedPassword = Objects.requireNonNull(hashedPassword, "The hashed password cannot be null.");
        if (username.isBlank()) {
            throw new IllegalArgumentException("The username cannot be empty.");
        }
        if (hashedPassword.isBlank()) {
            throw new IllegalArgumentException("The hashed password cannot be empty.");
        }
        this.mustChangePassword = mustChangePassword;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lockedUntil = lockedUntil;
    }

    // Create new account : force change password in first connexion
    public static Credentials register(String username, AccountId accountId,String hashedPassword) {
        return new Credentials(username, accountId, hashedPassword, true, 0, null);
    }

    public static Credentials reconstruct(String username, AccountId accountId, String hashedPassword, boolean mustChangePassword,
                                           int failedLoginAttempts, Instant lockedUntil) {
        return new Credentials(username, accountId, hashedPassword, mustChangePassword, failedLoginAttempts, lockedUntil);
    }

    public AccountId getAccountId() {
        return accountId;
    }
    
    public boolean isLocked() {
        return lockedUntil != null && Instant.now().isBefore(lockedUntil);
    }

    public boolean attemptLogin(String rawPassword, PasswordEncoder passwordEncoder) {
        if (isLocked()) {
            extendLock();
            return false;
        }
        if (passwordEncoder.matches(rawPassword, this.hashedPassword)) {
            resetFailedAttempts();
            return true;
        } else {
            registerFailedAttempt();
            return false;
        }
    }

    public void changePassword(String newHashedPassword) {
        if (newHashedPassword == null || newHashedPassword.isBlank()) {
            throw new IllegalArgumentException("The new hashed password cannot be empty.");
        }
        this.hashedPassword = newHashedPassword;
        this.mustChangePassword = false;
    }

    private void registerFailedAttempt() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
            extendLock();
        }
    }

    private void extendLock() {
        this.lockedUntil = Instant.now().plusSeconds(LOCK_DURATION_SECONDS);
    }

    private void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
    }

    public String getUsername() { return username; }
    public String getHashedPassword() { return hashedPassword; }
    public boolean mustChangePassword() { return mustChangePassword; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public Instant getLockedUntil() { return lockedUntil; }

    @Override
    public String id() {
        return username;
    }

    public interface PasswordEncoder {
        boolean matches(CharSequence rawPassword, String encodedPassword);
    }
}