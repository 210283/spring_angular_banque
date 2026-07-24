package com.votrebanque.application.port.outbound;

import java.util.Optional;

import com.votrebanque.domain.model.ActivationToken;

public interface ActivationTokenRepositoryPort {
    void save(ActivationToken token);
    Optional<ActivationToken> findByUsername(String username);
    Optional<ActivationToken> findById(String id); 
    //void delete(String username);
}
