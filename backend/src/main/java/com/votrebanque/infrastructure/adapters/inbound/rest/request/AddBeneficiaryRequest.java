package com.votrebanque.infrastructure.adapters.inbound.rest.request;

public record AddBeneficiaryRequest(
        String label,
        String accountNumber,
        String ownerName
) {
}
