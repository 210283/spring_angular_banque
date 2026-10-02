package com.votrebanque.service;

import java.util.List;

import com.votrebanque.application.port.inbound.CheckAccountAccessUseCase;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.infrastructure.security.AccountAccessGuard;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AccountAccessGuardTest {

    private final CheckAccountAccessUseCase checkAccountAccessUseCase = mock(CheckAccountAccessUseCase.class);
    private final AccountAccessGuard guard = new AccountAccessGuard(checkAccountAccessUseCase);

    @Test
    void deniesClientAccessToAnUnownedAccount() {
        var authentication = new UsernamePasswordAuthenticationToken(
            "client-1", "credentials", List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
        when(checkAccountAccessUseCase.canAccess("client-1", new AccountId("FR769876567"))).thenReturn(false);

        assertThatThrownBy(() -> guard.requireAccess(authentication, "FR769876567"))
            .isInstanceOf(AccessDeniedException.class);
        verify(checkAccountAccessUseCase).canAccess("client-1", new AccountId("FR769876567"));
    }

    @Test
    void permitsAdminAccessWithoutClientOwnershipLookup() {
        var authentication = new UsernamePasswordAuthenticationToken(
            "admin", "credentials", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        assertThatCode(() -> guard.requireAccess(authentication, "FR769876567")).doesNotThrowAnyException();
        verifyNoInteractions(checkAccountAccessUseCase);
    }
}
