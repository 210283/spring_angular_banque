package com.votrebanque.application.port.outbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Beneficiary;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepositoryPort {
    Optional<Beneficiary> findById(String id);

    Optional<Beneficiary> findByTargetAccountNumberAndOwnerAccountNumber(String targetAccountNumber, String ownerAccountNumber);

    List<Beneficiary> findAllByAccountNumber(AccountId accountNumber);

    Beneficiary save(AccountId sourceAccountId, Beneficiary beneficiary);
}
