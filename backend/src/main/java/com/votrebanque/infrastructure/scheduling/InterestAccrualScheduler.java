package com.votrebanque.infrastructure.scheduling;

import com.votrebanque.application.port.inbound.AccrueInterestUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InterestAccrualScheduler {

    private static final Logger log = LoggerFactory.getLogger(InterestAccrualScheduler.class);
    private final AccrueInterestUseCase accrueInterestUseCase;

    public InterestAccrualScheduler(AccrueInterestUseCase accrueInterestUseCase) {
        this.accrueInterestUseCase = accrueInterestUseCase;
    }

    @Scheduled(cron = "0 0 2 * * *") // Chaque nuit à 2h
    public void run() {
        int count = accrueInterestUseCase.accrueInterestForAllAccounts();
        log.info("Interest accrued for {} accounts", count);
    }
}
