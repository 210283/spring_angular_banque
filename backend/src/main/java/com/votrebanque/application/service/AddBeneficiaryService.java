package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.AddBeneficiaryUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.BeneficiaryRepositoryPort;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.Beneficiary;
import org.springframework.stereotype.Service;

@Service
public class AddBeneficiaryService implements AddBeneficiaryUseCase {

    private final AccountRepositoryPort bankAccountRepository;
    private final BeneficiaryRepositoryPort beneficiaryRepository;

    public AddBeneficiaryService(AccountRepositoryPort bankAccountRepository, BeneficiaryRepositoryPort beneficiaryRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @Override
    public Beneficiary addBeneficiary(AccountId sourceAccountId,String label, AccountId accountNumber, String ownerName) {
        Account beneficiaryAccount = bankAccountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Beneficiary account not found."));

        if (!beneficiaryAccount.owner().equals(ownerName)) {
            throw new IllegalArgumentException("The owner's name does not match the beneficiary account.");
        }

        Beneficiary beneficiary = Beneficiary.add(
                label,
                beneficiaryAccount.accountNumber(),
                sourceAccountId
        );

        return beneficiaryRepository.save(sourceAccountId, beneficiary);
    }
}