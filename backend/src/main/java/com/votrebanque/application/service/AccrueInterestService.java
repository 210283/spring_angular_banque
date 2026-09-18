package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.AccrueInterestUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.Money;


import java.time.LocalDate;
import java.util.List;

public class AccrueInterestService implements AccrueInterestUseCase {

    private final AccountRepositoryPort accountRepository;
    private final TransactionRecorder transactionRecorder;

    public AccrueInterestService(AccountRepositoryPort accountRepository, TransactionRecorder transactionRecorder) {
        this.accountRepository = accountRepository;
        this.transactionRecorder = transactionRecorder;
    }

    @Override
    public int accrueInterestForAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Account account : accounts) {
            Money interest = account.accrueInterest(today);
            accountRepository.save(account);

            if (!interest.isNegativeOrZero()) {
                transactionRecorder.recordInterest(account.accountNumber(), interest, account.balance());
            }
        }

        return accounts.size();
    }
}
