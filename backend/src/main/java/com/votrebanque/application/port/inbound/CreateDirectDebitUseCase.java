package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.DirectDebitFrequency;
import com.votrebanque.domain.model.Money;
import java.time.LocalDate;

public interface CreateDirectDebitUseCase {
    DirectDebitResult createDirectDebit(AccountId sourceAccountId, AccountId beneficiaryAccountId, Money amount,
                                         DirectDebitFrequency frequency, LocalDate startDate);

    record DirectDebitResult(String id, LocalDate nextExecutionDate) {}
}
