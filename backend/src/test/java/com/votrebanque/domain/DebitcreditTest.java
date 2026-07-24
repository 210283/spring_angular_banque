package com.votrebanque.domain;

import org.junit.jupiter.api.Test;

import com.votrebanque.domain.exception.InsufficientFundsException;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.Money;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

class DebitcreditTest {

    @Test
    void shouldCreditAccountSuccessfully() {
        // Given
        Account account = Account.open(new AccountId("FR761234567"), "John Doe", new Money(BigDecimal.valueOf(500.0)));
        Money amountToCredit = new Money(BigDecimal.valueOf(200.0));

        // When
        account.credit(amountToCredit);

        // Then
        assertThat(account.balance()).isEqualTo(new Money(BigDecimal.valueOf(700.0)));
    }

    @Test
    void shouldDebitAccountSuccessfully() {
        // Given
        Account account = Account.open(new AccountId("FR761234567"), "John Doe", new Money(BigDecimal.valueOf(500.0)));
        Money amountToDebit = new Money(BigDecimal.valueOf(200.0));

        // When
        account.debit(amountToDebit); 

        // Then
        assertThat(account.balance()).isEqualTo(new Money(BigDecimal.valueOf(300.0)));
    }
    
    @Test
    void shouldFailWhenDebitingMoreThanBalance() {
        // Given
        Account account = Account.open(new AccountId("FR761234567"), "John Doe", new Money(BigDecimal.valueOf(100.0)));
        
        // When & Then
        assertThatThrownBy(() -> account.debit(new Money(BigDecimal.valueOf(150.0))))
            .isInstanceOf(InsufficientFundsException.class);
    }
}
