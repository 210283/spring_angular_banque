package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Beneficiary;
import com.votrebanque.infrastructure.persistence.entity.BeneficiaryEntity;

public class BeneficiaryMapper {

    public static Beneficiary toDomain(BeneficiaryEntity entity) {
        return Beneficiary.reconstruct(
                entity.getId(),
                entity.getLabel(),
                new AccountId(entity.getTargetAccountNumber()),
                new AccountId(entity.getOwnerAccountNumber())
        );
    }

    public static BeneficiaryEntity toEntity(Beneficiary domain) {
        BeneficiaryEntity entity = new BeneficiaryEntity();
        entity.setId(domain.id());
        entity.setLabel(domain.label());
        entity.setTargetAccountNumber(domain.accountId().value());
        entity.setOwnerAccountNumber(domain.ownerId().value());
        return entity;
    }
}
