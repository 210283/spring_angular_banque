package com.votrebanque.service;

import com.votrebanque.application.service.TransferService;
import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.BeneficiaryRepositoryPort;
import com.votrebanque.domain.exception.InsufficientFundsException;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Beneficiary;
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
class TransferServiceTest {

    @Autowired
    private AccountRepositoryPort bankAccountRepository;

    @Autowired
    private BeneficiaryRepositoryPort beneficiaryRepository;

    @Autowired
    private TransferService transferService;

    final AccountId sourceId = new AccountId("FR761234567");
    final AccountId destinationId = new AccountId("FR769876589");

    @BeforeEach
    void setUp() {
        Account source = Account.open(sourceId, "Alice", new Money(BigDecimal.valueOf(1000.0)));
        Account destination = Account.open(destinationId, "John", new Money(BigDecimal.valueOf(600.0)));

        bankAccountRepository.save(source);
        bankAccountRepository.save(destination);

        // John doit être enregistré comme bénéficiaire d'Alice pour que le virement soit autorisé
        Beneficiary beneficiary = Beneficiary.add("John", destinationId, sourceId);
        beneficiaryRepository.save(sourceId, beneficiary);
    }

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