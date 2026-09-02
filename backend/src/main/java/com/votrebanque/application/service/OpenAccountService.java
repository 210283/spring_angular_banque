package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.AccountOpeningResult;
import com.votrebanque.application.port.inbound.AddBeneficiaryUseCase;
import com.votrebanque.application.port.inbound.OpenAccountUseCase;
import com.votrebanque.application.port.inbound.RegisterUserUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.domain.exception.AccountNotFoundException;
import com.votrebanque.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAccountService implements OpenAccountUseCase {

    private final AccountRepositoryPort bankAccountRepository;
    private final RegisterUserUseCase registerUserUseCase;
    private final AddBeneficiaryUseCase addBeneficiaryUseCase;

    @Override
    @Transactional
    public AccountOpeningResult openAccount(String owner, Money initialDeposit, AccountType accountType, String linkedAccountNumber) {

        String resolvedOwner;
        AccountId linkedAccountId = null;

        if (accountType.isSavings()) {
            if (linkedAccountNumber == null || linkedAccountNumber.isBlank()) {
                throw new IllegalArgumentException(
                    "A linked checking account is required to open a " + accountType.label());
            }

            linkedAccountId = new AccountId(linkedAccountNumber);
            Account linkedAccount = bankAccountRepository.findByNumber(linkedAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Linked checking account not found."));

            if (linkedAccount.accountType() != AccountType.CURRENT) {
                throw new IllegalArgumentException(
                    "A savings account can only be linked to a checking account (CURRENT).");
            }

            resolvedOwner = linkedAccount.owner();
        } else {
            if (owner == null || owner.isBlank()) {
                throw new IllegalArgumentException("The owner is required to open a checking account.");
            }
            resolvedOwner = owner;
        }

        AccountId newAccountId = new AccountId(AccountNumberGenerator.generate().value());

        Account newAccount = accountType.isSavings()
            ? Account.openSavings(newAccountId, resolvedOwner, initialDeposit, accountType)
            : Account.open(newAccountId, resolvedOwner, initialDeposit);

        bankAccountRepository.save(newAccount);

        // A savings account does not have its own login credentials: no credentials, no activation, and no email address.
        // It is immediately active, and only accessible via the linked checking account (beneficiary relationship).
        String username = null;
        String activationUrl = null;

        if (!accountType.isSavings()) {
            RegisterUserUseCase.RegistrationResult registration = registerUserUseCase.registerUser(newAccountId);
            username = registration.username();
            activationUrl = registration.activationUrl();
        }

        if (accountType.isSavings()) {
            addBeneficiaryUseCase.addBeneficiary(linkedAccountId, accountType.label(), newAccountId, resolvedOwner);
            addBeneficiaryUseCase.addBeneficiary(newAccountId, "Compte Courant", linkedAccountId, resolvedOwner);
        }

        return new AccountOpeningResult(newAccountId, username, activationUrl, accountType);
    }
}