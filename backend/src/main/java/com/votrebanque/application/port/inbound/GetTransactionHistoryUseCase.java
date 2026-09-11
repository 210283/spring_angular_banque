package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface GetTransactionHistoryUseCase {
    List<TransactionHistoryEntry> getTransactionHistory(AccountId accountNumber);

    record TransactionHistoryEntry(
        String id, String type, String category, BigDecimal amount, BigDecimal balanceAfter,
        String counterpartyAccountNumber, String counterpartyName, String label, Instant occurredAt
    ) {}
}