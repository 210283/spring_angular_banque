package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.AccountType;

public record BeneficiarySummary(String id, String label, AccountId accountId, String accountOwnerName, AccountType accountType) {
}
