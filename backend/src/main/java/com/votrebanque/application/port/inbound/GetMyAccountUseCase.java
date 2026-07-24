package com.votrebanque.application.port.inbound;

public interface GetMyAccountUseCase {
    AccountSummary getMyAccount(String username);
}
