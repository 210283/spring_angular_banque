package com.votrebanque.application.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.votrebanque.application.port.inbound.AddBeneficiaryUseCase;
import com.votrebanque.application.port.inbound.DemoSessionResult;
import com.votrebanque.application.port.inbound.OpenDemoSessionUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.port.outbound.DemoSessionRepositoryPort;
import com.votrebanque.application.port.outbound.PasswordEncoderPort;
import com.votrebanque.application.port.outbound.TokenProviderPort;
import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.AccountNumberGenerator;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.domain.model.DemoSession;
import com.votrebanque.domain.model.Money;

public class OpenDemoSessionService implements OpenDemoSessionUseCase {

    private static final Money MAIN_ACCOUNT_INITIAL_DEPOSIT = Money.from(1000);
    private static final Money SECONDARY_ACCOUNT_INITIAL_DEPOSIT = Money.from(300);

    private final AccountRepositoryPort accountRepository;
    private final CredentialsRepositoryPort credentialsRepository;
    private final DemoSessionRepositoryPort demoSessionRepository;
    private final AddBeneficiaryUseCase addBeneficiaryUseCase;
    private final TransactionRecorder transactionRecorder;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final Duration sessionDuration;

    public OpenDemoSessionService(AccountRepositoryPort accountRepository,
                                   CredentialsRepositoryPort credentialsRepository,
                                   DemoSessionRepositoryPort demoSessionRepository,
                                   AddBeneficiaryUseCase addBeneficiaryUseCase,
                                   TransactionRecorder transactionRecorder,
                                   PasswordEncoderPort passwordEncoder,
                                   TokenProviderPort tokenProvider,
                                   Duration sessionDuration) {
        this.accountRepository = accountRepository;
        this.credentialsRepository = credentialsRepository;
        this.demoSessionRepository = demoSessionRepository;
        this.addBeneficiaryUseCase = addBeneficiaryUseCase;
        this.transactionRecorder = transactionRecorder;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.sessionDuration = sessionDuration;
    }

    @Override
    public DemoSessionResult openDemoSession() {
        AccountId mainAccountId = new AccountId(AccountNumberGenerator.generate().value());
        AccountId secondaryAccountId = new AccountId(AccountNumberGenerator.generate().value());

        Account mainAccount = Account.open(mainAccountId, "Visiteur démo", MAIN_ACCOUNT_INITIAL_DEPOSIT);
        Account secondaryAccount = Account.open(secondaryAccountId, "Bénéficiaire démo", SECONDARY_ACCOUNT_INITIAL_DEPOSIT);

        accountRepository.save(mainAccount);
        accountRepository.save(secondaryAccount);

        transactionRecorder.recordDeposit(mainAccountId, MAIN_ACCOUNT_INITIAL_DEPOSIT, mainAccount.balance());
        transactionRecorder.recordDeposit(secondaryAccountId, SECONDARY_ACCOUNT_INITIAL_DEPOSIT, secondaryAccount.balance());

        addBeneficiaryUseCase.addBeneficiary(mainAccountId, "Bénéficiaire démo", secondaryAccountId, "Bénéficiaire démo");
        addBeneficiaryUseCase.addBeneficiary(secondaryAccountId, "Visiteur démo", mainAccountId, "Visiteur démo");

        String username = "demo-" + UUID.randomUUID();
        String randomPassword = UUID.randomUUID().toString();
        Credentials credentials = Credentials.register(username, mainAccountId, passwordEncoder.encode(randomPassword));
        credentials.changePassword(passwordEncoder.encode(randomPassword));
        credentialsRepository.save(credentials);

        Instant now = Instant.now();
        Instant expiresAt = now.plus(sessionDuration);
        demoSessionRepository.save(DemoSession.create(
            UUID.randomUUID().toString(),
            username,
            List.of(mainAccountId.value(), secondaryAccountId.value()),
            now,
            expiresAt
        ));

        String token = tokenProvider.generateToken(username, "ROLE_CLIENT");

        return new DemoSessionResult(token, mainAccountId.value(), secondaryAccountId.value(), expiresAt);
    }
}
