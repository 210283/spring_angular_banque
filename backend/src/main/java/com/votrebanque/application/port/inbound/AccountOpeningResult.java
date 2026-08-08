package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;

public record AccountOpeningResult(AccountId accountId, String username, String activationUrl) {}
