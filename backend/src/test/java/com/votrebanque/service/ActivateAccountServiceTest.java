package com.votrebanque.service;

import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.application.port.outbound.ActivationTokenRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.service.ActivateAccountService;
import com.votrebanque.domain.exception.InvalidOrExpiredTokenException;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.ActivationToken;
import com.votrebanque.domain.model.Credentials;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class ActivateAccountServiceTest {

    @Autowired
    private CredentialsRepositoryPort credentialsRepository;

    @Autowired
    private ActivationTokenRepositoryPort tokenRepository;

    @Autowired
    private ActivateAccountService activateAccountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    private static final String USERNAME = "12345678901";
    private static final AccountId ACCOUNT_ID = new AccountId("FR761234567");
    private static final String RAW_TOKEN = "raw-activation-token";
    private static final String VALID_NEW_PASSWORD = "ValidPass123";

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private String activationTokenId;

    private void givenRegisteredUserWithValidToken() {
        Credentials credentials = Credentials.register(USERNAME, ACCOUNT_ID, "$2a$10$temporaryHashedPassword");
        credentialsRepository.save(credentials);

        ActivationToken token = ActivationToken.generate(
            USERNAME, passwordEncoder.encode(RAW_TOKEN), Duration.ofHours(24)
        );
        activationTokenId = token.id();
        tokenRepository.save(token);

        flushAndClear();
    }

    @Test
    void shouldActivateAccountSuccessfullyWithValidTokenAndPassword() {
        givenRegisteredUserWithValidToken();

        activateAccountService.activateAccount(USERNAME, RAW_TOKEN, VALID_NEW_PASSWORD);

        flushAndClear();
        Credentials updatedCredentials = credentialsRepository.findByUsername(USERNAME).orElseThrow();
        assertThat(updatedCredentials.mustChangePassword()).isFalse();
        assertThat(passwordEncoder.matches(VALID_NEW_PASSWORD, updatedCredentials.getHashedPassword())).isTrue();

        ActivationToken updatedToken = tokenRepository.findById(activationTokenId).orElseThrow();
        assertThat(updatedToken.isUsed()).isTrue();
    }

    @Test
    void shouldThrowWhenTokenNotFound() {
        assertThatThrownBy(() -> activateAccountService.activateAccount(USERNAME, RAW_TOKEN, VALID_NEW_PASSWORD))
            .isInstanceOf(InvalidOrExpiredTokenException.class)
            .hasMessageContaining("Token not found");
    }

    @Test
    void shouldThrowWhenRawTokenIsIncorrect() {
        givenRegisteredUserWithValidToken();

        assertThatThrownBy(() ->
            activateAccountService.activateAccount(USERNAME, "wrong-raw-token", VALID_NEW_PASSWORD)
        )
            .isInstanceOf(InvalidOrExpiredTokenException.class)
            .hasMessageContaining("Invalid or expired token");

        flushAndClear();
        Credentials unchangedCredentials = credentialsRepository.findByUsername(USERNAME).orElseThrow();
        assertThat(unchangedCredentials.mustChangePassword()).isTrue();
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        Credentials credentials = Credentials.register(USERNAME, ACCOUNT_ID, "$2a$10$temporaryHashedPassword");
        credentialsRepository.save(credentials);

        ActivationToken expiredToken = ActivationToken.reconstruct(
            "expired-token-id",
            passwordEncoder.encode(RAW_TOKEN),
            USERNAME,
            Instant.now().minusSeconds(60),
            false,
            true
        );
        tokenRepository.save(expiredToken);

        flushAndClear();

        assertThatThrownBy(() -> activateAccountService.activateAccount(USERNAME, RAW_TOKEN, VALID_NEW_PASSWORD))
            .isInstanceOf(InvalidOrExpiredTokenException.class)
            .hasMessageContaining("Token not found");
    }

    @Test
    void shouldThrowWhenTokenAlreadyUsed() {
        Credentials credentials = Credentials.register(USERNAME, ACCOUNT_ID, "$2a$10$temporaryHashedPassword");
        credentialsRepository.save(credentials);

        ActivationToken usedToken = ActivationToken.reconstruct(
            "used-token-id",
            passwordEncoder.encode(RAW_TOKEN),
            USERNAME,
            Instant.now().plusSeconds(3600),
            true,
            true
        );
        tokenRepository.save(usedToken);

        flushAndClear();

        assertThatThrownBy(() -> activateAccountService.activateAccount(USERNAME, RAW_TOKEN, VALID_NEW_PASSWORD))
            .isInstanceOf(InvalidOrExpiredTokenException.class)
            .hasMessageContaining("Token not found");
    }

    @Test
    void shouldThrowWhenPasswordPolicyIsViolated() {
        givenRegisteredUserWithValidToken();

        assertThatThrownBy(() -> activateAccountService.activateAccount(USERNAME, RAW_TOKEN, "weak"))
            .isInstanceOf(IllegalArgumentException.class);

        flushAndClear();
        ActivationToken untouchedToken = tokenRepository.findByUsername(USERNAME).orElseThrow();
        assertThat(untouchedToken.isUsed()).isFalse();
    }

    @Test
    void shouldThrowWhenUserNotFoundDespiteValidToken() {
        ActivationToken orphanToken = ActivationToken.generate(
            USERNAME, passwordEncoder.encode(RAW_TOKEN), Duration.ofHours(24)
        );
        tokenRepository.save(orphanToken);

        flushAndClear();

        assertThatThrownBy(() -> activateAccountService.activateAccount(USERNAME, RAW_TOKEN, VALID_NEW_PASSWORD))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("User not found");
    }
}