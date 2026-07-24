package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.TransferUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.BeneficiaryRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Account;
//import com.votrebanque.domain.model.Beneficiary;
import com.votrebanque.domain.model.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {

    private final AccountRepositoryPort bankAccountRepository;
    private final BeneficiaryRepositoryPort beneficiaryRepository;

    @Override
    @Transactional
    public void makeTransfer(AccountId sourceAccountId, String destinationIdentifier, Money amount) {
        Account compteSource = bankAccountRepository.findByNumber(sourceAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found."));

        AccountId resolvedDestinationAccountId = resolveDestinationAccountId(sourceAccountId, destinationIdentifier);

        Account compteDestination = bankAccountRepository.findByNumber(resolvedDestinationAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found."));

        compteSource.debit(amount);
        compteDestination.credit(amount);

        bankAccountRepository.save(compteSource);
        bankAccountRepository.save(compteDestination);
    }

    private AccountId resolveDestinationAccountId(AccountId sourceAccountId, String destinationIdentifier) {
        if (destinationIdentifier == null || destinationIdentifier.isBlank()) {
            throw new IllegalArgumentException("The transfer destination is mandatory");
        }

        if (beneficiaryRepository == null) {
            return new AccountId(destinationIdentifier);
        }

        return beneficiaryRepository.findByTargetAccountNumberAndOwnerAccountNumber(destinationIdentifier, sourceAccountId.value())
                .map(entity -> entity.accountId().value())
                .map(AccountId::new)
                .orElseThrow(() -> new IllegalArgumentException("The recipient is not in your authorized beneficiary list."));
    }
}
