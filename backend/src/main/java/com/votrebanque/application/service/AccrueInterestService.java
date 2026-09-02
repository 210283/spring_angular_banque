package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.AccrueInterestUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.domain.model.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AccrueInterestService implements AccrueInterestUseCase {

    private final AccountRepositoryPort accountRepository;

    public AccrueInterestService(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public int accrueInterestForAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Account account : accounts) {
            account.accrueInterest(today);
            accountRepository.save(account);
        }

        return accounts.size();
    }
}
