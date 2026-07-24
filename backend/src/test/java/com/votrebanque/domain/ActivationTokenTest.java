package com.votrebanque.domain;

import com.votrebanque.domain.model.ActivationToken;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActivationTokenTest {

    // Simple test double implementing ActivationToken's own TokenEncoder interface
    private static class FakeTokenEncoder implements ActivationToken.TokenEncoder {
        private final String expectedRawToken;

        FakeTokenEncoder(String expectedRawToken) {
            this.expectedRawToken = expectedRawToken;
        }

        @Override
        public boolean matches(CharSequence rawToken, String hashedToken) {
            return rawToken.toString().equals(expectedRawToken);
        }
    }

    @Test
    void shouldGenerateValidTokenWithFutureExpiry() {
        // Given / When
        ActivationToken token = ActivationToken.generate("12345678901", "$2a$10$hashedToken", Duration.ofHours(24));

        // Then
        assertThat(token.getUsername()).isEqualTo("12345678901");
        assertThat(token.getHashedToken()).isEqualTo("$2a$10$hashedToken");
        assertThat(token.isUsed()).isFalse();
        assertThat(token.isEmailSent()).isFalse();
        assertThat(token.isValid()).isTrue();
        assertThat(token.getExpiresAt()).isAfter(Instant.now());
    }

    @Test
    void shouldRejectNullUsernameOnGenerate() {
        assertThatThrownBy(() -> ActivationToken.generate(null, "$2a$10$hashedToken", Duration.ofHours(24)))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("username cannot be null");
    }

    @Test
    void shouldRejectNullHashedTokenOnGenerate() {
        assertThatThrownBy(() -> ActivationToken.generate("12345678901", null, Duration.ofHours(24)))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("token cannot be null");
    }

    @Test
    void shouldRejectNullValidityOnGenerate() {
        assertThatThrownBy(() -> ActivationToken.generate("12345678901", "$2a$10$hashedToken", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("validity duration cannot be null");
    }

    @Test
    void shouldBeInvalidOnceExpired() {
        // Given : token whose expiry is already in the past
        ActivationToken token = ActivationToken.reconstruct(
            "some-id", "$2a$10$hashedToken", "12345678901",
            Instant.now().minusSeconds(60), false, false
        );

        // Then
        assertThat(token.isValid()).isFalse();
    }

    @Test
    void shouldBeInvalidOnceUsed() {
        // Given : token already marked as used
        ActivationToken token = ActivationToken.reconstruct(
            "some-id", "$2a$10$hashedToken", "12345678901",
            Instant.now().plusSeconds(3600), true, true
        );

        // Then
        assertThat(token.isValid()).isFalse();
    }

    @Test
    void shouldActivateSuccessfullyWithCorrectRawTokenBeforeExpiry() {
        // Given
        ActivationToken token = ActivationToken.generate("12345678901", "$2a$10$hashedToken", Duration.ofHours(24));
        ActivationToken.TokenEncoder encoder = new FakeTokenEncoder("correctRawToken");

        // When
        boolean result = token.attemptActivation("correctRawToken", encoder);

        // Then
        assertThat(result).isTrue();
        assertThat(token.isUsed()).isTrue();
        assertThat(token.isValid()).isFalse(); // used=true => no longer valid
    }

    @Test
    void shouldFailActivationWithWrongRawToken() {
        // Given
        ActivationToken token = ActivationToken.generate("12345678901", "$2a$10$hashedToken", Duration.ofHours(24));
        ActivationToken.TokenEncoder encoder = new FakeTokenEncoder("correctRawToken");

        // When
        boolean result = token.attemptActivation("wrongRawToken", encoder);

        // Then
        assertThat(result).isFalse();
        assertThat(token.isUsed()).isFalse();
        assertThat(token.isValid()).isTrue(); // still valid, not consumed
    }

    @Test
    void shouldFailActivationWhenAlreadyExpiredRegardlessOfCorrectToken() {
        // Given : expired token
        ActivationToken token = ActivationToken.reconstruct(
            "some-id", "$2a$10$hashedToken", "12345678901",
            Instant.now().minusSeconds(60), false, true
        );
        ActivationToken.TokenEncoder encoder = new FakeTokenEncoder("correctRawToken");

        // When
        boolean result = token.attemptActivation("correctRawToken", encoder);

        // Then
        assertThat(result).isFalse();
        assertThat(token.isUsed()).isFalse();
    }

    @Test
    void shouldFailActivationWhenAlreadyUsedRegardlessOfCorrectToken() {
        // Given : already-used token
        ActivationToken token = ActivationToken.reconstruct(
            "some-id", "$2a$10$hashedToken", "12345678901",
            Instant.now().plusSeconds(3600), true, true
        );
        ActivationToken.TokenEncoder encoder = new FakeTokenEncoder("correctRawToken");

        // When
        boolean result = token.attemptActivation("correctRawToken", encoder);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReconstructTokenWithGivenState() {
        // Given
        Instant expiresAt = Instant.now().plusSeconds(3600);

        // When
        ActivationToken token = ActivationToken.reconstruct(
            "existing-id", "$2a$10$hashedToken", "12345678901", expiresAt, false, true
        );

        // Then
        assertThat(token.id()).isEqualTo("existing-id");
        assertThat(token.getUsername()).isEqualTo("12345678901");
        assertThat(token.getHashedToken()).isEqualTo("$2a$10$hashedToken");
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(token.isUsed()).isFalse();
        assertThat(token.isEmailSent()).isTrue();
    }

    @Test
    void shouldMarkEmailAsSent() {
        // Given
        ActivationToken token = ActivationToken.generate("12345678901", "$2a$10$hashedToken", Duration.ofHours(24));
        assertThat(token.isEmailSent()).isFalse();

        // When
        token.markEmailSent();

        // Then
        assertThat(token.isEmailSent()).isTrue();
    }

    @Test
    void shouldNotAffectEmailSentWhenActivationFails() {
        // Given
        ActivationToken token = ActivationToken.generate("12345678901", "$2a$10$hashedToken", Duration.ofHours(24));
        token.markEmailSent();
        ActivationToken.TokenEncoder encoder = new FakeTokenEncoder("correctRawToken");

        // When
        token.attemptActivation("wrongRawToken", encoder);

        // Then : emailSent remains unchanged, independent of the activation result.
        assertThat(token.isEmailSent()).isTrue();
    }
}
