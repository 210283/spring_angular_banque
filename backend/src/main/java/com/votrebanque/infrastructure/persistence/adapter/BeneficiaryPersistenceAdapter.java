package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.application.port.outbound.BeneficiaryRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Beneficiary;
import com.votrebanque.infrastructure.persistence.entity.BeneficiaryEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataBeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BeneficiaryPersistenceAdapter implements BeneficiaryRepositoryPort {

    private final SpringDataBeneficiaryRepository jpaRepository;

    @Override
    public Optional<Beneficiary> findById(String id) {
        return jpaRepository.findById(id).map(BeneficiaryMapper::toDomain);
    }

    @Override
    public Optional<Beneficiary> findByTargetAccountNumberAndOwnerAccountNumber(String targetAccountNumber, String ownerAccountNumber) {
        return jpaRepository.findByTargetAccountNumberAndOwnerAccountNumber(targetAccountNumber, ownerAccountNumber)
                .map(BeneficiaryMapper::toDomain);
    }

    @Override
    public List<Beneficiary> findAllByAccountNumber(AccountId accountNumber) {
        return jpaRepository.findByOwnerAccountNumber(accountNumber.value())
                .stream()
                .map(BeneficiaryMapper::toDomain)
                .toList();
    }

    @Override
    public Beneficiary save(AccountId sourceAccountId, Beneficiary beneficiary) {
        BeneficiaryEntity entity = jpaRepository.findById(beneficiary.id())
                .map(existing -> {
                    existing.setLabel(beneficiary.label());
                    existing.setTargetAccountNumber(beneficiary.accountId().value());
                    existing.setOwnerAccountNumber(sourceAccountId.value());
                    return existing;
                })
                .orElseGet(() -> {
                    BeneficiaryEntity newEntity = BeneficiaryMapper.toEntity(beneficiary);
                    newEntity.setOwnerAccountNumber(sourceAccountId.value());
                    return newEntity;
                });

        BeneficiaryEntity savedEntity = jpaRepository.saveAndFlush(entity);
        return BeneficiaryMapper.toDomain(savedEntity);
    }
}