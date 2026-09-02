package com.votrebanque.infrastructure.adapters.inbound.rest.request;

import com.votrebanque.domain.model.AccountType;
import java.math.BigDecimal;

public record OpenAccountRequest(
    String owner,               // required if accountType == CURRENT, otherwise null
    BigDecimal initialDeposit,
    AccountType accountType,
    String linkedAccountNumber  // required if accountType != CURRENT, otherwise null
) {}