package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.DirectDebit;
import com.votrebanque.domain.model.Money;
import com.votrebanque.infrastructure.persistence.entity.DirectDebitEntity;

class DirectDebitMapper {

    static DirectDebit toDomain(DirectDebitEntity entity) {
        return DirectDebit.reconstruct(
            entity.getId(),
            new AccountId(entity.getSourceAccountNumber()),
            new AccountId(entity.getBeneficiaryAccountNumber()),
            entity.getBeneficiaryLabel(),
            new Money(entity.getAmount()),
            entity.getFrequency(),
            entity.getNextExecutionDate(),
            entity.isActive()
        );
    }

    static DirectDebitEntity toEntity(DirectDebit directDebit) {
        return new DirectDebitEntity(
            directDebit.id(),
            directDebit.sourceAccountId().value(),
            directDebit.beneficiaryAccountId().value(),
            directDebit.beneficiaryLabel(),
            directDebit.amount().amount(),
            directDebit.frequency(),
            directDebit.nextExecutionDate(),
            directDebit.active(),
            null
        );
    }
}
