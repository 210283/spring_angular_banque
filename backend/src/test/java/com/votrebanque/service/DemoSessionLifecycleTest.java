package com.votrebanque.service;

import java.time.Duration;
import java.util.List;

import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.application.port.inbound.AccountOpeningResult;
import com.votrebanque.application.port.inbound.CheckAccountAccessUseCase;
import com.votrebanque.application.port.inbound.DemoSessionResult;
import com.votrebanque.application.port.inbound.OpenAccountUseCase;
import com.votrebanque.application.port.inbound.OpenDemoSessionUseCase;
import com.votrebanque.application.port.inbound.PurgeExpiredDemoSessionsUseCase;
import com.votrebanque.application.port.inbound.TrackDemoAccountCreationUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.port.outbound.DemoSessionRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.AccountType;
import com.votrebanque.domain.model.DemoSession;
import com.votrebanque.domain.model.Money;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "app.demo.session.enabled=true",
    "app.demo.session.duration-minutes=120"
})
@Import(TestcontainersConfiguration.class)
@Transactional
class DemoSessionLifecycleTest {

    @Autowired
    private OpenDemoSessionUseCase openDemoSessionUseCase;

    @Autowired
    private PurgeExpiredDemoSessionsUseCase purgeExpiredDemoSessionsUseCase;

    @Autowired
    private TrackDemoAccountCreationUseCase trackDemoAccountCreationUseCase;

    @Autowired
    private OpenAccountUseCase openAccountUseCase;

    @Autowired
    private CheckAccountAccessUseCase checkAccountAccessUseCase;

    @Autowired
    private AccountRepositoryPort accountRepository;

    @Autowired
    private CredentialsRepositoryPort credentialsRepository;

    @Autowired
    private DemoSessionRepositoryPort demoSessionRepository;

    @Autowired
    private EntityManager entityManager;

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldIsolateTwoDemoSessionsFromEachOther() {
        DemoSessionResult sessionA = openDemoSessionUseCase.openDemoSession();
        DemoSessionResult sessionB = openDemoSessionUseCase.openDemoSession();
        flushAndClear();

        String usernameA = findUsernameByAccount(sessionA.mainAccountNumber());
        String usernameB = findUsernameByAccount(sessionB.mainAccountNumber());

        assertThat(checkAccountAccessUseCase.canAccess(usernameA, new AccountId(sessionA.mainAccountNumber()))).isTrue();

        // The secondary account is only a transfer target (like a beneficiary), not an owned account.
        assertThat(checkAccountAccessUseCase.canAccess(usernameA, new AccountId(sessionA.secondaryAccountNumber()))).isFalse();

        assertThat(checkAccountAccessUseCase.canAccess(usernameA, new AccountId(sessionB.mainAccountNumber()))).isFalse();
        assertThat(checkAccountAccessUseCase.canAccess(usernameB, new AccountId(sessionA.mainAccountNumber()))).isFalse();
    }

    @Test
    void shouldDeleteAllDataWhenASessionExpires() {
        DemoSessionResult session = openDemoSessionUseCase.openDemoSession();
        flushAndClear();

        String username = findUsernameByAccount(session.mainAccountNumber());

        // Force the session to be already expired, as if it had been created in the past.
        DemoSession expiredSession = DemoSession.reconstruct(
            findSessionId(session.mainAccountNumber()),
            List.of(username),
            List.of(session.mainAccountNumber(), session.secondaryAccountNumber()),
            java.time.Instant.now().minus(Duration.ofHours(3)),
            java.time.Instant.now().minus(Duration.ofHours(1))
        );
        demoSessionRepository.save(expiredSession);
        flushAndClear();

        int purged = purgeExpiredDemoSessionsUseCase.purgeExpiredSessions();
        flushAndClear();

        assertThat(purged).isEqualTo(1);
        assertThat(accountRepository.findByNumber(new AccountId(session.mainAccountNumber()))).isEmpty();
        assertThat(accountRepository.findByNumber(new AccountId(session.secondaryAccountNumber()))).isEmpty();
        assertThat(credentialsRepository.findByUsername(username)).isEmpty();
    }

    @Test
    void demoAdminCanOpenAnAccountButCannotAccessOtherAccounts() {
        DemoSessionResult session = openDemoSessionUseCase.openDemoSession();
        flushAndClear();

        String adminUsername = findAdminUsername(session.mainAccountNumber());

        assertThat(checkAccountAccessUseCase.canAccess(adminUsername, new AccountId(session.mainAccountNumber()))).isFalse();

        AccountOpeningResult opened = openAccountUseCase.openAccount("Nouveau client démo", Money.from(500), AccountType.CURRENT, "");
        trackDemoAccountCreationUseCase.trackAccountOpenedByDemoAdmin(adminUsername, opened.accountId().value(), opened.username());
        flushAndClear();

        DemoSession updatedSession = demoSessionRepository.findByUsername(adminUsername).orElseThrow();
        assertThat(updatedSession.getAccountNumbers()).contains(opened.accountId().value());
        assertThat(updatedSession.getUsernames()).contains(opened.username());

        // The demo admin still cannot read the account it just opened: creation rights only, no ownership bypass.
        assertThat(checkAccountAccessUseCase.canAccess(adminUsername, opened.accountId())).isFalse();
    }

    private String findAdminUsername(String mainAccountNumber) {
        return demoSessionRepository.findExpired(java.time.Instant.now().plus(Duration.ofDays(3650))).stream()
            .filter(s -> s.getAccountNumbers().contains(mainAccountNumber))
            .flatMap(s -> s.getUsernames().stream())
            .filter(username -> username.startsWith("demo-admin-"))
            .findFirst()
            .orElseThrow();
    }

    private String findUsernameByAccount(String accountNumber) {
        return demoSessionRepository.findExpired(java.time.Instant.now().plus(Duration.ofDays(3650))).stream()
            .filter(s -> s.getAccountNumbers().contains(accountNumber))
            .flatMap(s -> s.getUsernames().stream())
            .filter(username -> !username.startsWith("demo-admin-"))
            .findFirst()
            .orElseThrow();
    }

    private String findSessionId(String accountNumber) {
        return demoSessionRepository.findExpired(java.time.Instant.now().plus(Duration.ofDays(3650))).stream()
            .filter(s -> s.getAccountNumbers().contains(accountNumber))
            .map(DemoSession::id)
            .findFirst()
            .orElseThrow();
    }
}
