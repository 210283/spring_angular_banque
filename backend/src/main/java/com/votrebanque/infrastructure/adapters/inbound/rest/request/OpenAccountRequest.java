package com.votrebanque.infrastructure.adapters.inbound.rest.request;

import java.math.BigDecimal;

public record OpenAccountRequest(
    String owner,
    BigDecimal initialDeposit
) {}