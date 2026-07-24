package com.votrebanque.service;

import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.service.LoginService;
import com.votrebanque.domain.exception.InvalidCredentialsException;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.infrastructure.security.config.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class LoginServiceTest {

    @Autowired
    private CredentialsRepositoryPort credentialsRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EntityManager entityManager;

    private static final String CLIENT_USERNAME = "12345678901";
    private static final String CLIENT_PASSWORD = "ValidPass123";
    private static final AccountId ACCOUNT_ID = new AccountId("FR761234567");

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private void givenActivatedClient() {
        Credentials credentials = Credentials.register(
            CLIENT_USERNAME, ACCOUNT_ID, passwordEncoder.encode("temporaryPassword")
        );
        credentials.changePassword(passwordEncoder.encode(CLIENT_PASSWORD));
        credentialsRepository.save(credentials);

        flushAndClear();
    }

    @Test
    void shouldGenerateAdminTokenWithValidStaffCredentials() {
        // When
        String token = loginService.login("admin", "password123");

        // Then
        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.extractUsername(token)).isEqualTo("admin");
        assertThat(jwtTokenProvider.extractRole(token)).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldThrowWhenAdminPasswordIsWrong() {
        assertThatThrownBy(() -> loginService.login("admin", "wrongPassword"))
            .isInstanceOf(InvalidCredentialsException.class)
            .hasMessageContaining("Invalid username or password");
    }

    @Test
    void shouldGenerateClientTokenWithValidClientCredentials() {
        // Given
        givenActivatedClient();

        // When
        String token = loginService.login(CLIENT_USERNAME, CLIENT_PASSWORD);

        // Then
        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.extractUsername(token)).isEqualTo(CLIENT_USERNAME);
        assertThat(jwtTokenProvider.extractRole(token)).isEqualTo("ROLE_CLIENT");
    }

    @Test
    void shouldThrowWhenClientUsernameDoesNotExist() {
        assertThatThrownBy(() -> loginService.login("99999999999", CLIENT_PASSWORD))
            .isInstanceOf(InvalidCredentialsException.class)
            .hasMessageContaining("Invalid username or password");
    }

    @Test
    void shouldThrowWhenClientPasswordIsWrong() {
        // Given
        givenActivatedClient();

        // When & Then
        assertThatThrownBy(() -> loginService.login(CLIENT_USERNAME, "wrongPassword"))
            .isInstanceOf(InvalidCredentialsException.class)
            .hasMessageContaining("Invalid username or password");

        // Then : the failed attempt is persisted (lockout tracking)
        flushAndClear();
        Credentials updated = credentialsRepository.findByUsername(CLIENT_USERNAME).orElseThrow();
        assertThat(updated.getFailedLoginAttempts()).isEqualTo(1);
    }

    @Test
    void shouldLockClientAccountAfterFiveFailedAttempts() {
        // Given
        givenActivatedClient();

        // When
        for (int i = 0; i < 5; i++) {
            try {
                loginService.login(CLIENT_USERNAME, "wrongPassword");
            } catch (InvalidCredentialsException ignored) {
                // expected on each attempt
            }
        }

        // Then
        flushAndClear();
        Credentials locked = credentialsRepository.findByUsername(CLIENT_USERNAME).orElseThrow();
        assertThat(locked.isLocked()).isTrue();

        // Even the correct password now fails while locked
        assertThatThrownBy(() -> loginService.login(CLIENT_USERNAME, CLIENT_PASSWORD))
            .isInstanceOf(InvalidCredentialsException.class);
    }
}
