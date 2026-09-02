package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.AccountType;

public record AccountOpeningResult(AccountId accountId, String username, String activationUrl, AccountType accountType) {}