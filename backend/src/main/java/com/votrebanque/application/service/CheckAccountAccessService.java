package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.CheckAccountAccessUseCase;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.port.outbound.LinkedSavingsAccountRepositoryPort;
import com.votrebanque.domain.model.AccountId;

public class CheckAccountAccessService implements CheckAccountAccessUseCase {

    private final CredentialsRepositoryPort credentialsRepository;
    private final LinkedSavingsAccountRepositoryPort linkedSavingsAccountRepository;

    public CheckAccountAccessService(CredentialsRepositoryPort credentialsRepository,
                                     LinkedSavingsAccountRepositoryPort linkedSavingsAccountRepository) {
        this.credentialsRepository = credentialsRepository;
        this.linkedSavingsAccountRepository = linkedSavingsAccountRepository;
    }

    @Override
    public boolean canAccess(String username, AccountId accountId) {
        return credentialsRepository.findByUsername(username)
            .map(credentials -> credentials.getAccountId().equals(accountId)
                || linkedSavingsAccountRepository.isLinkedSavingsAccount(credentials.getAccountId(), accountId))
            .orElse(false);
    }
}
