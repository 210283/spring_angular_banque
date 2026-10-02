package com.votrebanque.infrastructure.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.votrebanque.application.port.inbound.PurgeExpiredDemoSessionsUseCase;

@Component
public class DemoSessionCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(DemoSessionCleanupScheduler.class);
    private final PurgeExpiredDemoSessionsUseCase purgeExpiredDemoSessionsUseCase;

    public DemoSessionCleanupScheduler(PurgeExpiredDemoSessionsUseCase purgeExpiredDemoSessionsUseCase) {
        this.purgeExpiredDemoSessionsUseCase = purgeExpiredDemoSessionsUseCase;
    }

    @Scheduled(fixedRateString = "${app.demo.session.cleanup-interval-ms:900000}")
    public void run() {
        int count = purgeExpiredDemoSessionsUseCase.purgeExpiredSessions();
        if (count > 0) {
            log.info("{} expired demo session(s) purged", count);
        }
    }
}
