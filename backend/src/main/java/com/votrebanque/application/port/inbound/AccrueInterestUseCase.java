package com.votrebanque.application.port.inbound;

public interface AccrueInterestUseCase {
    int accrueInterestForAllAccounts();  // returns the number of accounts processed
}
