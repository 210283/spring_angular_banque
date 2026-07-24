package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Beneficiary;

public interface AddBeneficiaryUseCase {
    Beneficiary addBeneficiary(AccountId sourceAccountId, String label, AccountId accountNumber, String ownerName);
}
