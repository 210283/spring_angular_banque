package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.*;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.port.outbound.LinkedSavingsAccountRepositoryPort;
import com.votrebanque.domain.model.AccountType;
import com.votrebanque.domain.model.Credentials;

import java.util.List;

public class GetLinkedSavingsAccountsService implements GetLinkedSavingsAccountsUseCase {

    private final CredentialsRepositoryPort credentialsRepository;
    private final LinkedSavingsAccountRepositoryPort linkedSavingsAccountRepository;
    private final AccountRepositoryPort accountRepository;

    public GetLinkedSavingsAccountsService(CredentialsRepositoryPort credentialsRepository,
                                            LinkedSavingsAccountRepositoryPort linkedSavingsAccountRepository,
                                            AccountRepositoryPort accountRepository) {
        this.credentialsRepository = credentialsRepository;
        this.linkedSavingsAccountRepository = linkedSavingsAccountRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountSummary> getLinkedSavingsAccounts(String username) {
        Credentials credentials = credentialsRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalStateException("User not found"));

        return linkedSavingsAccountRepository.findSavingsAccountIds(credentials.getAccountId()).stream()
            .map(accountRepository::findByNumber)
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
            .filter(account -> account.accountType() != AccountType.CURRENT) // Exclude checking accounts
            .map(account -> new AccountSummary(
                account.accountNumber(), account.owner(), account.balance(),
                account.accountType(), account.interestRate()))
            .toList();
    }
}