package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;

public record BeneficiarySummary(String id, String label, AccountId accountId, String accountOwnerName) {
}
