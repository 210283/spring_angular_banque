package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;

public interface RegisterUserUseCase {
    String registerUser(AccountId accountId);
}