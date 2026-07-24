package com.votrebanque.service;

import com.votrebanque.application.service.OpenAccountService;
import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.application.port.inbound.AccountOpeningResult;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)@Transactional
class OpenAccountServiceTest {

    @Autowired
    private AccountRepositoryPort bankAccountRepository;

    @Autowired
    private OpenAccountService openAccountService;

    @Test
    void shouldOpenAccountSuccessfullyWithGeneratedId() {
        // Given
        String owner = "Charlie";
        Money initialDeposit = new Money(BigDecimal.valueOf(250.0));

        // When
        AccountOpeningResult newAccount = openAccountService.openAccount(owner, initialDeposit);

        // Then
        assertThat(newAccount.accountId().value()).matches("^FR76\\d{7}$");

        Account savedAccount = bankAccountRepository.findByNumber(newAccount.accountId()).orElseThrow();
        assertThat(savedAccount.owner()).isEqualTo("Charlie");
        assertThat(savedAccount.balance()).isEqualTo(initialDeposit);
    }

    @Test
    void shouldFailToOpenAccountWhenInitialDepositIsNegative() {
        String owner = "Charlie";
        Money negativeDeposit = new Money(BigDecimal.valueOf(-50.0));

        assertThatThrownBy(() -> openAccountService.openAccount(owner, negativeDeposit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The initial deposit must be greater than 20.00");
    }

    @Test
    void shouldFailToOpenAccountWhenInitialDepositIsZero() {
        String owner = "Charlie";
        Money zeroDeposit = new Money(BigDecimal.valueOf(0.0));

        assertThatThrownBy(() -> openAccountService.openAccount(owner, zeroDeposit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The initial deposit must be greater than 20.00");
    }
}