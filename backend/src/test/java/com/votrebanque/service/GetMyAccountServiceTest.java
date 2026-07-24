package com.votrebanque.service;

import com.votrebanque.application.port.inbound.AccountSummary;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.service.GetMyAccountService;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.domain.model.Money;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@Transactional
class GetMyAccountServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private CredentialsRepositoryPort credentialsRepository;

    @Autowired
    private AccountRepositoryPort accountRepository;

    @Autowired
    private GetMyAccountService getMyAccountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    private static final String USERNAME = "12345678901";
    private static final AccountId ACCOUNT_ID = new AccountId("FR761234567");

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private void givenClientWithAccount() {
        Account account = Account.open(ACCOUNT_ID, "Charlie", new Money(BigDecimal.valueOf(150.00)));
        accountRepository.save(account);

        Credentials credentials = Credentials.register(USERNAME, ACCOUNT_ID, passwordEncoder.encode("temporaryPassword"));
        credentialsRepository.save(credentials);

        flushAndClear();
    }

    @Test
    void shouldReturnAccountSummaryForKnownUser() {
        // Given
        givenClientWithAccount();

        // When
        AccountSummary result = getMyAccountService.getMyAccount(USERNAME);

        // Then
        assertThat(result.accountId()).isEqualTo(ACCOUNT_ID);
        assertThat(result.owner()).isEqualTo("Charlie");
        assertThat(result.balance().amount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenCredentialsNotFound() {
        // Given : no credentials registered for this username

        // When & Then
        assertThatThrownBy(() -> getMyAccountService.getMyAccount("99999999999"))
            .isInstanceOf(AccountNotFoundException.class);
    }
}
