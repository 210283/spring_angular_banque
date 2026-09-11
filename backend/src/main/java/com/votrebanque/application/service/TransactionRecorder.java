package com.votrebanque.application.service;

import com.votrebanque.application.port.outbound.TransactionRepositoryPort;
import com.votrebanque.domain.model.*;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class TransactionRecorder {

    private final TransactionRepositoryPort transactionRepository;

    public TransactionRecorder(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void recordDeposit(AccountId accountNumber, Money amount, Money balanceAfter) {
        transactionRepository.save(TransactionRecord.record(
            accountNumber, TransactionType.CREDIT, TransactionCategory.OPENING_DEPOSIT,
            amount, balanceAfter, null, null, "Opening deposit", null
        ));
    }

    public void recordTransfer(AccountId sourceAccountNumber, Money sourceBalanceAfter,
                                AccountId destinationAccountNumber, Money destinationBalanceAfter,
                                Money amount, String sourceOwnerName, String destinationOwnerName,
                                TransactionCategory category, String label) {
        String correlationId = UUID.randomUUID().toString();

        transactionRepository.save(TransactionRecord.record(
            sourceAccountNumber, TransactionType.DEBIT, category,
            amount, sourceBalanceAfter, destinationAccountNumber, destinationOwnerName, label, correlationId
        ));

        transactionRepository.save(TransactionRecord.record(
            destinationAccountNumber, TransactionType.CREDIT, category,
            amount, destinationBalanceAfter, sourceAccountNumber, sourceOwnerName, label, correlationId
        ));
    }

    public void recordInterest(AccountId accountNumber, Money interestAmount, Money balanceAfter) {
        if (interestAmount.isNegativeOrZero()) return;
        transactionRepository.save(TransactionRecord.record(
            accountNumber, TransactionType.CREDIT, TransactionCategory.INTEREST,
            interestAmount, balanceAfter, null, null, "Interest credited", null
        ));
    }
}
