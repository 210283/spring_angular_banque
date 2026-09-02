package com.votrebanque.application.port.inbound;

import java.util.List;

public interface GetLinkedSavingsAccountsUseCase {
    List<AccountSummary> getLinkedSavingsAccounts(String username);
}
