package com.votrebanque.application.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.votrebanque.application.port.inbound.PurgeExpiredDemoSessionsUseCase;
import com.votrebanque.application.port.outbound.DemoCleanupPort;
import com.votrebanque.application.port.outbound.DemoSessionRepositoryPort;
import com.votrebanque.domain.model.DemoSession;

public class PurgeExpiredDemoSessionsService implements PurgeExpiredDemoSessionsUseCase {

    private final DemoSessionRepositoryPort demoSessionRepository;
    private final DemoCleanupPort demoCleanupPort;

    public PurgeExpiredDemoSessionsService(DemoSessionRepositoryPort demoSessionRepository,
                                            DemoCleanupPort demoCleanupPort) {
        this.demoSessionRepository = demoSessionRepository;
        this.demoCleanupPort = demoCleanupPort;
    }

    @Override
    public int purgeExpiredSessions() {
        List<DemoSession> expiredSessions = demoSessionRepository.findExpired(Instant.now());

        for (DemoSession session : expiredSessions) {
            List<String> accountNumbers = new ArrayList<>(session.getAccountNumbers());
            demoCleanupPort.deleteAccountsAndCredentials(accountNumbers, session.getUsernames());
            demoSessionRepository.deleteById(session.id());
        }

        return expiredSessions.size();
    }
}
