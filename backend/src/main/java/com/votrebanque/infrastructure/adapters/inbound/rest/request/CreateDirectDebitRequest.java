package com.votrebanque.infrastructure.adapters.inbound.rest.request;

import com.votrebanque.domain.model.DirectDebitFrequency;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDirectDebitRequest(
    String beneficiaryAccountNumber, BigDecimal amount, DirectDebitFrequency frequency, LocalDate startDate
) {}
