package com.votrebanque.application.port.outbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.TransactionRecord;
import java.util.List;

public interface TransactionRepositoryPort {
    void save(TransactionRecord transaction);
    List<TransactionRecord> findAllByAccountNumberOrderByOccurredAtDesc(AccountId accountNumber);
}
