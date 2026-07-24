package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Money;

public interface TransferUseCase {

    void makeTransfer(AccountId sourceAccountId, String destinationIdentifier, Money montant);
}