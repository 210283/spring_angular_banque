package com.votrebanque.infrastructure.adapters.inbound.rest.response;

import com.votrebanque.application.port.inbound.AccountOpeningResult;

public record AccountCreationResponse(String accountId, String username, String activationUrl) {

    public static AccountCreationResponse from(AccountOpeningResult result) {
        return new AccountCreationResponse(
            result.accountId().value(),
            result.username(),
            result.activationUrl()
        );
    }
}
