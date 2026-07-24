package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Money;
import com.votrebanque.domain.model.Account;
import com.votrebanque.infrastructure.persistence.entity.AccountEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final SpringDataAccountRepository repository;

    public AccountPersistenceAdapter(SpringDataAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Account account) {
        AccountEntity entity = repository.findById(account.accountNumber().value())
            .map(existing -> {
                existing.setOwner(account.owner());
                existing.setBalance(account.balance().amount());
                return existing;
            })
            .orElseGet(() -> AccountMapper.toEntity(account));

        repository.save(entity);
    }

    @Override
    public Optional<Account> findByNumber(AccountId accountId) {
        return repository.findById(accountId.value())
            .map(AccountMapper::toDomain);
    }
}
