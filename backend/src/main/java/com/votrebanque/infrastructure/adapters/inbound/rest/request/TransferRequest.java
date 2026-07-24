package com.votrebanque.infrastructure.adapters.inbound.rest.request;

import java.math.BigDecimal;

public record TransferRequest(
    String sourceAccountNumber,
    String destinationAccountNumber,
    BigDecimal amount
) {}