package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Money;
import com.votrebanque.infrastructure.persistence.entity.AccountEntity;

class AccountMapper {
    static Account toDomain(AccountEntity entity) {
        return Account.reconstruct(
            new AccountId(entity.getAccountNumber()),
            entity.getOwner(),
            new Money(entity.getBalance()),
            entity.getAccountType(),
            entity.getInterestRate(),
            entity.getLastInterestAccrualDate()
        );
    }

    static AccountEntity toEntity(Account account) {
        return new AccountEntity(
            account.accountNumber().value(),
            account.owner(),
            account.balance().amount(),
            account.accountType(),
            account.interestRate(),
            account.lastInterestAccrualDate(),
            null
        );
    }
}