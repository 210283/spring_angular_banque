package com.votrebanque.infrastructure.adapters.inbound.rest.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DirectDebitResponse(
    String id, String beneficiaryAccountNumber, String beneficiaryLabel,
    BigDecimal amount, String frequency, LocalDate nextExecutionDate, boolean active
) {}
