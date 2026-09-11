package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.CancelDirectDebitUseCase;
import com.votrebanque.application.port.outbound.DirectDebitRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.DirectDebit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelDirectDebitService implements CancelDirectDebitUseCase {

    private final DirectDebitRepositoryPort repository;

    public CancelDirectDebitService(DirectDebitRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void cancelDirectDebit(AccountId accountNumber, String directDebitId) {
        DirectDebit directDebit = repository.findById(directDebitId)
            .orElseThrow(() -> new IllegalArgumentException("Direct debit not found."));

        if (!directDebit.sourceAccountId().equals(accountNumber)) {
            throw new IllegalArgumentException("This direct debit does not belong to the specified account.");
        }

        directDebit.cancel();
        repository.save(directDebit);
    }
}
