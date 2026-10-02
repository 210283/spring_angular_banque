package com.votrebanque.application.port.outbound;

import java.util.List;

import com.votrebanque.domain.model.AccountId;

public interface LinkedSavingsAccountRepositoryPort {
    void link(AccountId currentAccountId, AccountId savingsAccountId);
    boolean isLinkedSavingsAccount(AccountId currentAccountId, AccountId savingsAccountId);
    List<AccountId> findSavingsAccountIds(AccountId currentAccountId);
}
