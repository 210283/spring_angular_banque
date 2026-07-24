package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Money;

public record AccountSummary(
        AccountId accountId,
        String owner,
        Money balance
) {
}
