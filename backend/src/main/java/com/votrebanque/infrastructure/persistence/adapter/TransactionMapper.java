package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Money;
import com.votrebanque.domain.model.TransactionRecord;
import com.votrebanque.infrastructure.persistence.entity.TransactionEntity;

class TransactionMapper {

    static TransactionRecord toDomain(TransactionEntity entity) {
        return TransactionRecord.reconstruct(
            entity.getId(),
            new AccountId(entity.getAccountNumber()),
            entity.getType(),
            entity.getCategory(),
            new Money(entity.getAmount()),
            new Money(entity.getBalanceAfter()),
            entity.getCounterpartyAccountNumber() != null ? new AccountId(entity.getCounterpartyAccountNumber()) : null,
            entity.getCounterpartyName(),
            entity.getLabel(),
            entity.getOccurredAt(),
            entity.getCorrelationId()
        );
    }

    static TransactionEntity toEntity(TransactionRecord record) {
        return new TransactionEntity(
            record.id(),
            record.accountNumber().value(),
            record.type(),
            record.category(),
            record.amount().amount(),
            record.balanceAfter().amount(),
            record.counterpartyAccountNumber() != null ? record.counterpartyAccountNumber().value() : null,
            record.counterpartyName(),
            record.label(),
            record.occurredAt(),
            record.correlationId()
        );
    }
}
