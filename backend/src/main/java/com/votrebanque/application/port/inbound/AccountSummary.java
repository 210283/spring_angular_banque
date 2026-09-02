package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.AccountType;
import com.votrebanque.domain.model.Money;
import java.math.BigDecimal;

public record AccountSummary(
        AccountId accountId,
        String owner,
        Money balance,
        AccountType accountType,
        BigDecimal interestRate
) {}
