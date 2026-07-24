package com.votrebanque.service;

import com.votrebanque.application.service.TransferService;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.domain.exception.InsufficientFundsException;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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
class TransferServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private AccountRepositoryPort bankAccountRepository;

    @Autowired
    private TransferService transferService;

    final AccountId sourceId = new AccountId("FR761234567");
    final AccountId destinationId = new AccountId("FR769876589");

    @Test
    void shouldExecuteTransferSuccessfully() {

        Money amount = new Money(BigDecimal.valueOf(200.0));
        transferService.makeTransfer(sourceId, destinationId.value(), amount);

        Account updatedSource = bankAccountRepository.findByNumber(sourceId).orElseThrow();
        Account updatedDest = bankAccountRepository.findByNumber(destinationId).orElseThrow();

        assertThat(updatedSource.balance()).isEqualTo(new Money(BigDecimal.valueOf(800.0)));
        assertThat(updatedDest.balance()).isEqualTo(new Money(BigDecimal.valueOf(800.0)));
    }

    @Test
    void shouldFailTransferWhenFundsAreInsufficient() {
        Money excessiveAmount = new Money(BigDecimal.valueOf(1500.0));

        assertThatThrownBy(() -> transferService.makeTransfer(sourceId, destinationId.value(), excessiveAmount))
                .isInstanceOf(InsufficientFundsException.class);

        Account finalSource = bankAccountRepository.findByNumber(sourceId).orElseThrow();
        Account finalDest = bankAccountRepository.findByNumber(destinationId).orElseThrow();

        assertThat(finalSource.balance()).isEqualTo(new Money(BigDecimal.valueOf(1000.0)));
        assertThat(finalDest.balance()).isEqualTo(new Money(BigDecimal.valueOf(600.0)));
    }
}