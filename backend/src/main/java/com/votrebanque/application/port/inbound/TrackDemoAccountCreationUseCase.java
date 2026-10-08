package com.votrebanque.application.port.inbound;

public interface TrackDemoAccountCreationUseCase {
    void trackAccountOpenedByDemoAdmin(String demoAdminUsername, String newAccountNumber, String newUsername);
}
