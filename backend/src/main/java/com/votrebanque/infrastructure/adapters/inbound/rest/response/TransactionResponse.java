package com.votrebanque.infrastructure.adapters.inbound.rest.response;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
    String id, String type, String category, BigDecimal amount, BigDecimal balanceAfter,
    String counterpartyAccountNumber, String counterpartyName, String label, Instant occurredAt
) {}
