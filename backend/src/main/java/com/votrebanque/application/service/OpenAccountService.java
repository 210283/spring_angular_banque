package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.AccountOpeningResult;
import com.votrebanque.application.port.inbound.OpenAccountUseCase;
import com.votrebanque.application.port.inbound.RegisterUserUseCase;
import com.votrebanque.application.port.outbound.AccountRepositoryPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.AccountNumberGenerator;
import com.votrebanque.domain.model.Money;
import com.votrebanque.domain.model.UsernameGenerator;
import com.votrebanque.domain.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
@RequiredArgsConstructor
public class OpenAccountService implements OpenAccountUseCase {

    private final AccountRepositoryPort bankAccountRepository;
    private final RegisterUserUseCase registerUserUseCase;

    @Override
    @Transactional
    public AccountOpeningResult openAccount(String owner, Money initialDeposit) {
        
        // Generate a unique identifier for the new account
        AccountId newAccountId = new AccountId(AccountNumberGenerator.generate().value());

        // Instantiate the domain object (it is responsible for validating the business invariants!)
        Account newAccount = Account.open(newAccountId, owner, initialDeposit);

        // Save the aggregate via the output port.
        bankAccountRepository.save(newAccount);

        // Generate the client's login username (11 random digits)
        String username = registerUserUseCase.registerUser(newAccountId);

        // Return the ID so that the REST controller can send it to front
        return new AccountOpeningResult(newAccountId, username);
    }
}