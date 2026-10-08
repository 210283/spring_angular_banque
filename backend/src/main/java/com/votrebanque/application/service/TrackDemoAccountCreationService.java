package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.TrackDemoAccountCreationUseCase;
import com.votrebanque.application.port.outbound.DemoSessionRepositoryPort;
import com.votrebanque.domain.model.DemoSession;

public class TrackDemoAccountCreationService implements TrackDemoAccountCreationUseCase {

    private final DemoSessionRepositoryPort demoSessionRepository;

    public TrackDemoAccountCreationService(DemoSessionRepositoryPort demoSessionRepository) {
        this.demoSessionRepository = demoSessionRepository;
    }

    @Override
    public void trackAccountOpenedByDemoAdmin(String demoAdminUsername, String newAccountNumber, String newUsername) {
        demoSessionRepository.findByUsername(demoAdminUsername).ifPresent(session -> {
            DemoSession updated = session.withAdditionalAccount(newAccountNumber, newUsername);
            demoSessionRepository.save(updated);
        });
    }
}
