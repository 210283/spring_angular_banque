package com.votrebanque.service;

import com.votrebanque.application.service.AddBeneficiaryService;
import com.votrebanque.application.service.TransferService;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.BeneficiaryRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort; // <-- Ajout de l'import
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.Beneficiary;
import com.votrebanque.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean; 
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
class BeneficiaryServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @MockitoBean
    private CredentialsRepositoryPort credentialsRepositoryPort;
    // ----------------------------------------------

    @Autowired
    private AccountRepositoryPort bankAccountRepository;

    @Autowired
    private BeneficiaryRepositoryPort beneficiaryRepository;

    @Autowired
    private AddBeneficiaryService addBeneficiaryService;

    @Autowired
    private TransferService transferService;

    final AccountId sourceId = new AccountId("FR761234567");
    final AccountId destinationId = new AccountId("FR769876567");

    @Test
    void shouldAddBeneficiaryAndTransferToIt() {
        Money amount = new Money(BigDecimal.valueOf(200.0));

        Beneficiary beneficiary = addBeneficiaryService.addBeneficiary(sourceId, "ben1", destinationId, "Bob");

        transferService.makeTransfer(sourceId, beneficiary.accountId().value(), amount);

        Account updatedSource = bankAccountRepository.findByNumber(sourceId).orElseThrow();
        Account updatedDestination = bankAccountRepository.findByNumber(destinationId).orElseThrow();

        assertThat(beneficiary.accountId().value()).isEqualTo(destinationId.value());
        assertThat(updatedSource.balance().amount()).isEqualByComparingTo(BigDecimal.valueOf(800.0));
        assertThat(updatedDestination.balance().amount()).isEqualByComparingTo(BigDecimal.valueOf(700.0));
    }

    @Test
    void shouldNotAddBeneficiaryWhenAccountDoesNotExist() {
        AccountId nonExistentDestinationId = new AccountId("FR760000000");

        assertThatThrownBy(() -> addBeneficiaryService.addBeneficiary(
                sourceId, "Alice", nonExistentDestinationId, "Unknown"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void shouldNotAddBeneficiaryWhenOwnerNotMatch() {

        assertThatThrownBy(() -> addBeneficiaryService.addBeneficiary(sourceId, "Alice", destinationId, "Charlie"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The owner's name does not match the beneficiary account.");
    }
}