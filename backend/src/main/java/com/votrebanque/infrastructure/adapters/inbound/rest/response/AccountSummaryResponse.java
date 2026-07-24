package com.votrebanque.infrastructure.adapters.inbound.rest.response;

import java.math.BigDecimal;

public record AccountSummaryResponse(
        String accountId,
        String owner,
        BigDecimal balance
) {
}
