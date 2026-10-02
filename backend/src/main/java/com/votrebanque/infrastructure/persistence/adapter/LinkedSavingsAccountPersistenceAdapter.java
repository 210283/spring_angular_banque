package com.votrebanque.infrastructure.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.votrebanque.application.port.outbound.LinkedSavingsAccountRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.infrastructure.persistence.entity.LinkedSavingsAccountEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataLinkedSavingsAccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LinkedSavingsAccountPersistenceAdapter implements LinkedSavingsAccountRepositoryPort {

    private final SpringDataLinkedSavingsAccountRepository repository;

    @Override
    public void link(AccountId currentAccountId, AccountId savingsAccountId) {
        repository.save(new LinkedSavingsAccountEntity(savingsAccountId.value(), currentAccountId.value()));
    }

    @Override
    public boolean isLinkedSavingsAccount(AccountId currentAccountId, AccountId savingsAccountId) {
        return repository.existsByCurrentAccountNumberAndSavingsAccountNumber(
            currentAccountId.value(), savingsAccountId.value()
        );
    }

    @Override
    public List<AccountId> findSavingsAccountIds(AccountId currentAccountId) {
        return repository.findAllByCurrentAccountNumber(currentAccountId.value()).stream()
            .map(link -> new AccountId(link.getSavingsAccountNumber()))
            .toList();
    }
}
