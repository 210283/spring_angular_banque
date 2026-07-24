package com.votrebanque.service;

import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.application.port.outbound.ActivationTokenRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.service.RegisterUserService;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class RegisterUserServiceTests {

    @MockitoSpyBean
    private CredentialsRepositoryPort credentialsRepository;

    @Autowired
    private ActivationTokenRepositoryPort tokenRepository;

    @Autowired
    private RegisterUserService registerUserService;

    private final AccountId accountId = new AccountId("FR769854210");

    @Test
    void shouldSuccessfullyRegisterUser() {
        String generatedUsername = registerUserService.registerUser(accountId);

        assertThat(generatedUsername).isNotNull().isNotEmpty();
        assertThat(generatedUsername).matches("^\\d{11}$");

        Credentials savedCredentials = credentialsRepository.findByUsername(generatedUsername).orElseThrow();
        assertThat(savedCredentials.getUsername()).isEqualTo(generatedUsername);
        assertThat(savedCredentials.getAccountId()).isEqualTo(accountId);
        assertThat(savedCredentials.mustChangePassword()).isTrue();

        assertThat(tokenRepository.findByUsername(generatedUsername)).isPresent();
    }

    @Test
    void shouldRetryUsernameGenerationWhenConflictExists() {
        Credentials existingCredentials = Credentials.register("00000000000", accountId, "irrelevantHash");
        credentialsRepository.save(existingCredentials);

        when(credentialsRepository.findByUsername(anyString()))
            .thenReturn(Optional.of(existingCredentials))
            .thenCallRealMethod();

        String generatedUsername = registerUserService.registerUser(accountId);

        assertThat(generatedUsername).isNotNull();
        assertThat(generatedUsername).isNotEqualTo("00000000000");
        assertThat(credentialsRepository.findByUsername(generatedUsername)).isPresent();
    }

    @Test
    void shouldThrowExceptionWhenMaxGenerationAttemptsReached() {
        Credentials existingCredentials = Credentials.register("00000000000", accountId, "irrelevantHash");

        when(credentialsRepository.findByUsername(anyString()))
            .thenReturn(Optional.of(existingCredentials));

        assertThatThrownBy(() -> registerUserService.registerUser(accountId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Unable to generate a unique customer ID after 10 attempts");
    }
}