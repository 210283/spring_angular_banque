package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.GetDirectDebitsUseCase;
import com.votrebanque.application.port.outbound.DirectDebitRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.DirectDebit;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetDirectDebitsService implements GetDirectDebitsUseCase {

    private final DirectDebitRepositoryPort repository;

    public GetDirectDebitsService(DirectDebitRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<DirectDebit> getDirectDebits(AccountId accountNumber) {
        return repository.findAllBySourceAccountId(accountNumber);
    }
}
