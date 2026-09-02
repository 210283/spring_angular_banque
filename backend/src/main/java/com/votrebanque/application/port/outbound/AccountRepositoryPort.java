package com.votrebanque.application.port.outbound;

import com.votrebanque.domain.model.Account;
import com.votrebanque.domain.model.AccountId;
import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {

    Optional<Account> findByNumber(AccountId numeroCompte);

    List<Account> findAll();

    void save(Account bankAccount);
}