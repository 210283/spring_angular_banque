package com.votrebanque.application.port.inbound;

public interface ActivateAccountUseCase {
    void activateAccount(String username, String rawToken, String chosenPassword);
}
