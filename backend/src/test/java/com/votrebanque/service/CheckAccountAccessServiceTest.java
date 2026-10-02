package com.votrebanque.service;

import java.util.Optional;

import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.port.outbound.LinkedSavingsAccountRepositoryPort;
import com.votrebanque.application.service.CheckAccountAccessService;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Credentials;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckAccountAccessServiceTest {

    private static final String USERNAME = "client-1";
    private static final AccountId CURRENT_ACCOUNT = new AccountId("FR761234567");
    private static final AccountId LINKED_SAVINGS_ACCOUNT = new AccountId("FR761234568");
    private static final AccountId OTHER_ACCOUNT = new AccountId("FR769876567");

    private final CredentialsRepositoryPort credentialsRepository = mock(CredentialsRepositoryPort.class);
    private final LinkedSavingsAccountRepositoryPort linkedSavingsAccountRepository =
        mock(LinkedSavingsAccountRepositoryPort.class);
    private final CheckAccountAccessService service =
        new CheckAccountAccessService(credentialsRepository, linkedSavingsAccountRepository);

    @Test
    void allowsThePrimaryAccount() {
        givenCredentials();

        assertThat(service.canAccess(USERNAME, CURRENT_ACCOUNT)).isTrue();
    }

    @Test
    void allowsAnExplicitlyLinkedSavingsAccount() {
        givenCredentials();
        when(linkedSavingsAccountRepository.isLinkedSavingsAccount(CURRENT_ACCOUNT, LINKED_SAVINGS_ACCOUNT))
            .thenReturn(true);

        assertThat(service.canAccess(USERNAME, LINKED_SAVINGS_ACCOUNT)).isTrue();
    }

    @Test
    void deniesAnUnlinkedAccount() {
        givenCredentials();
        when(linkedSavingsAccountRepository.isLinkedSavingsAccount(CURRENT_ACCOUNT, OTHER_ACCOUNT))
            .thenReturn(false);

        assertThat(service.canAccess(USERNAME, OTHER_ACCOUNT)).isFalse();
    }

    @Test
    void deniesUnknownUsers() {
        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThat(service.canAccess(USERNAME, CURRENT_ACCOUNT)).isFalse();
    }

    private void givenCredentials() {
        when(credentialsRepository.findByUsername(USERNAME)).thenReturn(Optional.of(
            Credentials.reconstruct(USERNAME, CURRENT_ACCOUNT, "encoded-password", false, 0, null)
        ));
    }
}
