package com.votrebanque.infrastructure.adapters.inbound.rest.response;

import com.votrebanque.application.port.inbound.AccountOpeningResult;
import com.votrebanque.domain.model.AccountType;

public record AccountCreationResponse(String accountId, String username, String activationUrl, AccountType accountType) {

    public static AccountCreationResponse from(AccountOpeningResult result) {
        return new AccountCreationResponse(
            result.accountId().value(),
            result.username(),
            result.activationUrl(),
            result.accountType()
        );
    }
}
