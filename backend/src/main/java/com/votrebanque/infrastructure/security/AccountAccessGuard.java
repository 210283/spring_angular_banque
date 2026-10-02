package com.votrebanque.infrastructure.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.votrebanque.application.port.inbound.CheckAccountAccessUseCase;
import com.votrebanque.domain.model.AccountId;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountAccessGuard {

    private final CheckAccountAccessUseCase checkAccountAccessUseCase;

    public void requireAccess(Authentication authentication, String accountNumber) {
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !checkAccountAccessUseCase.canAccess(authentication.getName(), new AccountId(accountNumber))) {
            throw new AccessDeniedException("You cannot access this account.");
        }
    }
}
