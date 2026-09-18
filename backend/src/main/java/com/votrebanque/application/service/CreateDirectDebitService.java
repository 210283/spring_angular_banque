package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.CreateDirectDebitUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.BeneficiaryRepositoryPort;
import com.votrebanque.application.port.outbound.DirectDebitRepositoryPort;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.model.*;
import java.time.LocalDate;

public class CreateDirectDebitService implements CreateDirectDebitUseCase {

    private final DirectDebitRepositoryPort directDebitRepository;
    private final AccountRepositoryPort accountRepository;
    private final BeneficiaryRepositoryPort beneficiaryRepository;

    public CreateDirectDebitService(DirectDebitRepositoryPort directDebitRepository,
                                     AccountRepositoryPort accountRepository,
                                     BeneficiaryRepositoryPort beneficiaryRepository) {
        this.directDebitRepository = directDebitRepository;
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @Override
    public DirectDebitResult createDirectDebit(AccountId sourceAccountId, AccountId beneficiaryAccountId,
                                                Money amount, DirectDebitFrequency frequency, LocalDate startDate) {

        accountRepository.findByNumber(sourceAccountId)
            .orElseThrow(() -> new AccountNotFoundException("Source account not found."));

        var beneficiary = beneficiaryRepository
            .findByTargetAccountNumberAndOwnerAccountNumber(beneficiaryAccountId.value(), sourceAccountId.value())
            .orElseThrow(() -> new IllegalArgumentException("The recipient is not in your authorized beneficiary list."));

        DirectDebit directDebit = DirectDebit.create(sourceAccountId, beneficiaryAccountId, beneficiary.label(),
            amount, frequency, startDate);

        directDebitRepository.save(directDebit);

        return new DirectDebitResult(directDebit.id(), directDebit.nextExecutionDate());
    }
}
