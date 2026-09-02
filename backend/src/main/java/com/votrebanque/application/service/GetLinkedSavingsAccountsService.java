package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.*;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.BeneficiaryRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.domain.model.AccountType;
import com.votrebanque.domain.model.Credentials;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetLinkedSavingsAccountsService implements GetLinkedSavingsAccountsUseCase {

    private final CredentialsRepositoryPort credentialsRepository;
    private final BeneficiaryRepositoryPort beneficiaryRepository;
    private final AccountRepositoryPort accountRepository;

    public GetLinkedSavingsAccountsService(CredentialsRepositoryPort credentialsRepository,
                                            BeneficiaryRepositoryPort beneficiaryRepository,
                                            AccountRepositoryPort accountRepository) {
        this.credentialsRepository = credentialsRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountSummary> getLinkedSavingsAccounts(String username) {
        Credentials credentials = credentialsRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalStateException("User not found"));

        return beneficiaryRepository.findAllByAccountNumber(credentials.getAccountId()).stream()
            .map(beneficiary -> accountRepository.findByNumber(beneficiary.accountId()))
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
            .filter(account -> account.accountType() != AccountType.CURRENT) // Exclude checking accounts
            .map(account -> new AccountSummary(
                account.accountNumber(), account.owner(), account.balance(),
                account.accountType(), account.interestRate()))
            .toList();
    }
}