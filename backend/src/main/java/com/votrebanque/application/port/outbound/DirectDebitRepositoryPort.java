package com.votrebanque.application.port.outbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.DirectDebit;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DirectDebitRepositoryPort {
    void save(DirectDebit directDebit);
    Optional<DirectDebit> findById(String id);
    List<DirectDebit> findAllBySourceAccountId(AccountId sourceAccountId);
    List<DirectDebit> findAllDueOn(LocalDate date);
}
