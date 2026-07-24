package com.votrebanque.application.port.outbound;

import java.util.Optional;

import com.votrebanque.domain.model.Credentials;

public interface CredentialsRepositoryPort {
    void save(Credentials credentials);
    Optional<Credentials> findByUsername(String username);
}
