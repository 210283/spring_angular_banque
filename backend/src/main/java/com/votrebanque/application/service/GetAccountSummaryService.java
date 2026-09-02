package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.AccountSummary;
import com.votrebanque.application.port.inbound.GetAccountSummaryUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAccountSummaryService implements GetAccountSummaryUseCase {

    private final AccountRepositoryPort bankAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountSummary getAccountSummary(AccountId accountId) {
        Account bankAccount = bankAccountRepository.findByNumber(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found."));
        return new AccountSummary(
                bankAccount.accountNumber(),
                bankAccount.owner(),
                bankAccount.balance(),
                bankAccount.accountType(),
                bankAccount.interestRate()
        );
    }
}
