package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.GetTransactionHistoryUseCase;
import com.votrebanque.application.port.outbound.TransactionRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import java.util.List;

public class GetTransactionHistoryService implements GetTransactionHistoryUseCase {

    private final TransactionRepositoryPort transactionRepository;

    public GetTransactionHistoryService(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<TransactionHistoryEntry> getTransactionHistory(AccountId accountNumber) {
        return transactionRepository.findAllByAccountNumberOrderByOccurredAtDesc(accountNumber).stream()
            .map(t -> new TransactionHistoryEntry(
                t.id(), t.type().name(), t.category().name(), t.amount().amount(), t.balanceAfter().amount(),
                t.counterpartyAccountNumber() != null ? t.counterpartyAccountNumber().value() : null,
                t.counterpartyName(), t.label(), t.occurredAt()
            ))
            .toList();
    }
}
