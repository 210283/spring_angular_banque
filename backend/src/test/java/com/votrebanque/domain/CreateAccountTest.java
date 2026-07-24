package com.votrebanque.domain;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.Money;

import static org.assertj.core.api.Assertions.*;

public class CreateAccountTest {
    
    @Test
    void shouldCreateAccountWithValidIban() {
        // Given
        AccountId accountId = new AccountId("FR761234567");
        Money initialDeposit = new Money(BigDecimal.valueOf(100.0));

        // When
        Account account = Account.open(accountId, "Charlie", initialDeposit);

        // Then
        assertThat(account.accountNumber().value()).startsWith("FR76");
        assertThat(account.owner()).isEqualTo("Charlie");
        assertThat(account.balance()).isEqualTo(initialDeposit);
    }

    @Test
    void shouldFailWhenInitialDepositIsLessThanTwenty() {
        // Given
        AccountId accountId = new AccountId("FR761234567");
        Money negativeDeposit = new Money(BigDecimal.valueOf(19.0));

        // When & Then
        assertThatThrownBy(() -> Account.open(accountId, "Charlie", negativeDeposit))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("The initial deposit must be greater than 20.00");
    }

    @Test
    void shouldFailWhenCreateAccountWithNonValidIban() {

        // When & Then
        assertThatThrownBy(() -> new AccountId("FR76123"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid account number format. Must be FR76 followed by 7 digits.");
    }
}
