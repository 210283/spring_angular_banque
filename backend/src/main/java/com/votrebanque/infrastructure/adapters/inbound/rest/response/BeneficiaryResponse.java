package com.votrebanque.infrastructure.adapters.inbound.rest.response;

import com.votrebanque.domain.model.AccountType;

public record BeneficiaryResponse(String id, String label, String beneficiaryAccountNumber, String accountName, AccountType accountType) {
}
