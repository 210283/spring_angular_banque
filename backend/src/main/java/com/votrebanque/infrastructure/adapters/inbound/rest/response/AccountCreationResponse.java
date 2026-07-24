package com.votrebanque.infrastructure.adapters.inbound.rest.response;

import com.votrebanque.application.port.inbound.AccountOpeningResult;

public record AccountCreationResponse(String accountId, String username) {

    public static AccountCreationResponse from(AccountOpeningResult result) {
        return new AccountCreationResponse(
            result.accountId().value(),
            result.username()
        );
    }
}
