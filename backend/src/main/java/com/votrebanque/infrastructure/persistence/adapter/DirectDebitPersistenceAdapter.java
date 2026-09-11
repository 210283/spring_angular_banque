package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.application.port.outbound.DirectDebitRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.DirectDebit;
import com.votrebanque.infrastructure.persistence.entity.DirectDebitEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataDirectDebitRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class DirectDebitPersistenceAdapter implements DirectDebitRepositoryPort {

    private final SpringDataDirectDebitRepository repository;

    public DirectDebitPersistenceAdapter(SpringDataDirectDebitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(DirectDebit directDebit) {
        DirectDebitEntity entity = repository.findById(directDebit.id())
            .map(existing -> {
                existing.setNextExecutionDate(directDebit.nextExecutionDate());
                existing.setActive(directDebit.active());
                return existing;
            })
            .orElseGet(() -> DirectDebitMapper.toEntity(directDebit));

        repository.save(entity);
    }

    @Override
    public Optional<DirectDebit> findById(String id) {
        return repository.findById(id).map(DirectDebitMapper::toDomain);
    }

    @Override
    public List<DirectDebit> findAllBySourceAccountId(AccountId sourceAccountId) {
        return repository.findAllBySourceAccountNumber(sourceAccountId.value()).stream()
            .map(DirectDebitMapper::toDomain)
            .toList();
    }

    @Override
    public List<DirectDebit> findAllDueOn(LocalDate date) {
        return repository.findAllByActiveTrueAndNextExecutionDateLessThanEqual(date).stream()
            .map(DirectDebitMapper::toDomain)
            .toList();
    }
}
