package com.votrebanque.domain;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Credentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class RegisterUserTest {

    private static final AccountId ACCOUNT_ID = new AccountId("FR761234567");

    @Test
    void shouldRegisterCredentialsWithMustChangePasswordTrue() {
        // Given
        String username = "12345678901";
        String hashedPassword = "$2a$10$hashedvalue";

        // When
        Credentials credentials = Credentials.register(username, ACCOUNT_ID, hashedPassword);

        // Then
        assertThat(credentials.getUsername()).isEqualTo(username);
        assertThat(credentials.getAccountId()).isEqualTo(ACCOUNT_ID);
        assertThat(credentials.getHashedPassword()).isEqualTo(hashedPassword);
        assertThat(credentials.mustChangePassword()).isTrue();
        assertThat(credentials.getFailedLoginAttempts()).isZero();
        assertThat(credentials.isLocked()).isFalse();
    }

    @Test
    void shouldLockAccountAfterFiveFailedAttempts() {
        Credentials credentials = Credentials.register("12345678901", ACCOUNT_ID, "$2a$10$hashedvalue");

        Credentials.PasswordEncoder mockEncoder = (rawPassword, encodedPassword) -> false;

        for (int i = 0; i < 5; i++) {
            credentials.attemptLogin("wrongPassword", mockEncoder);
        }

        // Then
        assertThat(credentials.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(credentials.isLocked()).isTrue();
        assertThat(credentials.getLockedUntil()).isAfter(Instant.now());
    }

    @Test
    void shouldResetFailedAttemptsOnSuccessfulLogin() {
        // Given
        Credentials credentials = Credentials.register("12345678901", ACCOUNT_ID, "$2a$10$hashedvalue");
        Credentials.PasswordEncoder failEncoder = (raw, hashed) -> false;
        Credentials.PasswordEncoder successEncoder = (raw, hashed) -> true;

        credentials.attemptLogin("wrongPassword", failEncoder);
        assertThat(credentials.getFailedLoginAttempts()).isEqualTo(1);

        // When
        boolean loginSuccess = credentials.attemptLogin("correctPassword", successEncoder);

        // Then
        assertThat(loginSuccess).isTrue();
        assertThat(credentials.getFailedLoginAttempts()).isZero();
        assertThat(credentials.isLocked()).isFalse();
    }

    @Test
    void shouldChangePasswordAndClearMustChangeFlag() {
        // Given
        Credentials credentials = Credentials.register("12345678901", ACCOUNT_ID, "$2a$10$oldHashed");
        String newHashedPassword = "$2a$10$newHashed";

        // When
        credentials.changePassword(newHashedPassword);

        // Then
        assertThat(credentials.getHashedPassword()).isEqualTo(newHashedPassword);
        assertThat(credentials.mustChangePassword()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenRegisteringWithEmptyValues() {
        assertThatThrownBy(() -> Credentials.register("", ACCOUNT_ID, "$2a$10$hash"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Credentials.register("12345678901", ACCOUNT_ID, "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}