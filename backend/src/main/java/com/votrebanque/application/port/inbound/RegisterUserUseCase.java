package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;

public interface RegisterUserUseCase {
    RegistrationResult registerUser(AccountId accountId);

    record RegistrationResult(String username, String activationUrl) {}
}