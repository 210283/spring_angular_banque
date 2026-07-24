package com.votrebanque.service;

import com.votrebanque.application.service.GetAccountSummaryService;
import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.application.port.inbound.AccountSummary;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class GetAccountSummaryServiceTest {

    @Autowired
    private AccountRepositoryPort bankAccountRepository;

    @Autowired
    private GetAccountSummaryService getAccountSummaryService;

    @BeforeEach
    void setUp() {
        bankAccountRepository.save(Account.open(
                new AccountId("FR761234567"),
                "Alice",
                new Money(BigDecimal.valueOf(1000.00))
        ));
    }

    @Test
    void shouldReturnAccountSummaryWhenAccountExists() {
        AccountSummary summary = getAccountSummaryService.getAccountSummary(new AccountId("FR761234567"));

        assertThat(summary.accountId().value()).isEqualTo("FR761234567");
        assertThat(summary.owner()).isEqualTo("Alice");
        assertThat(summary.balance()).isEqualTo(new Money(BigDecimal.valueOf(1000.00)));
    }

    @Test
    void shouldFailWhenAccountDoesNotExist() {
        assertThatThrownBy(() -> getAccountSummaryService.getAccountSummary(new AccountId("FR760000000")))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account not found");
    }
}
