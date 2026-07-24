package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.AccountId;

public interface GetAccountSummaryUseCase {

    AccountSummary getAccountSummary(AccountId accountId);
}
