package com.votrebanque.application.service;

import org.springframework.stereotype.Service;
import com.votrebanque.application.port.inbound.AccountSummary;
import com.votrebanque.application.port.inbound.GetAccountSummaryUseCase;
import com.votrebanque.application.port.inbound.GetMyAccountUseCase;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.model.Credentials;

@Service
public class GetMyAccountService implements GetMyAccountUseCase {

    private final CredentialsRepositoryPort credentialsRepository;
    private final GetAccountSummaryUseCase getAccountSummaryUseCase;

    public GetMyAccountService(CredentialsRepositoryPort credentialsRepository,
                                GetAccountSummaryUseCase getAccountSummaryUseCase) {
        this.credentialsRepository = credentialsRepository;
        this.getAccountSummaryUseCase = getAccountSummaryUseCase;
    }

    @Override
    public AccountSummary getMyAccount(String username) {
        Credentials credentials = credentialsRepository.findByUsername(username)
            .orElseThrow(() -> new AccountNotFoundException("No client account associated with this user"));

        return getAccountSummaryUseCase.getAccountSummary(credentials.getAccountId());
    }
}
