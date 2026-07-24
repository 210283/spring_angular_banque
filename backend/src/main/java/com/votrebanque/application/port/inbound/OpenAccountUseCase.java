package com.votrebanque.application.port.inbound;

import com.votrebanque.domain.model.Money;

public interface OpenAccountUseCase {
    
    /**
     * Opens a new bank account with an initial deposit.
     * * @param owner The name of the account holder.
     * @param initialDeposit The initial money deposited into the account.
     * @return The unique AccountId generated for the new account.
     */
    AccountOpeningResult openAccount(String owner, Money initialDeposit);
}
