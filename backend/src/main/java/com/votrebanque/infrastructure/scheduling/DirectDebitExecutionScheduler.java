package com.votrebanque.infrastructure.scheduling;

import com.votrebanque.application.port.inbound.ExecuteDirectDebitsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DirectDebitExecutionScheduler {

    private static final Logger log = LoggerFactory.getLogger(DirectDebitExecutionScheduler.class);
    private final ExecuteDirectDebitsUseCase executeDirectDebitsUseCase;

    public DirectDebitExecutionScheduler(ExecuteDirectDebitsUseCase executeDirectDebitsUseCase) {
        this.executeDirectDebitsUseCase = executeDirectDebitsUseCase;
    }

    @Scheduled(cron = "0 30 2 * * *") // each night at 2:30 AM, after interest calculation
    public void run() {
        int count = executeDirectDebitsUseCase.executeDueDirectDebits();
        log.info("{} direct debit(s) executed", count);
    }
}
