package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.application.port.outbound.TransactionRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.TransactionRecord;
import com.votrebanque.infrastructure.persistence.repository.SpringDataTransactionRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {

    private final SpringDataTransactionRepository repository;

    public TransactionPersistenceAdapter(SpringDataTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(TransactionRecord transaction) {
        // Ledger append-only : Each row has a unique UUID that is never modified -> direct insert, no reload necessary
        repository.save(TransactionMapper.toEntity(transaction));
    }

    @Override
    public List<TransactionRecord> findAllByAccountNumberOrderByOccurredAtDesc(AccountId accountNumber) {
        return repository.findAllByAccountNumberOrderByOccurredAtDesc(accountNumber.value()).stream()
            .map(TransactionMapper::toDomain)
            .toList();
    }
}
