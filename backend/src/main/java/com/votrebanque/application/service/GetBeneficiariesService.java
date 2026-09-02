package com.votrebanque.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.votrebanque.application.port.inbound.BeneficiarySummary;
import com.votrebanque.application.port.inbound.GetBeneficiariesUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.BeneficiaryRepositoryPort;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.model.AccountId;

@Service
public class GetBeneficiariesService implements GetBeneficiariesUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepository;
    private final AccountRepositoryPort accountRepository;

    public GetBeneficiariesService(BeneficiaryRepositoryPort beneficiaryRepository,
                                    AccountRepositoryPort accountRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<BeneficiarySummary> getBeneficiaries(AccountId accountId) {
        return beneficiaryRepository.findAllByAccountNumber(accountId).stream()
            .map(beneficiary -> {
                var targetAccount = accountRepository.findByNumber(beneficiary.accountId())
                    .orElseThrow(() -> new AccountNotFoundException(
                        "Beneficiary target account not found: " + beneficiary.accountId().value()
                    ));
                return new BeneficiarySummary(
                    beneficiary.id(),
                    beneficiary.label(),
                    beneficiary.accountId(),
                    targetAccount.owner(),
                    targetAccount.accountType()
                );
            })
            .toList();
    }
}
