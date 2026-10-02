package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;

public interface CheckAccountAccessUseCase {
    boolean canAccess(String username, AccountId accountId);
}
