package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.infrastructure.persistence.entity.CredentialsEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataCredentialsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CredentialsPersistenceAdapter implements CredentialsRepositoryPort {

    private final SpringDataCredentialsRepository repository;

    public CredentialsPersistenceAdapter(SpringDataCredentialsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Credentials credentials) {
        CredentialsEntity entity = repository.findById(credentials.getUsername())
            .<CredentialsEntity>map(existing -> CredentialsMapper.updateEntity(existing, credentials))
            .orElseGet(() -> CredentialsMapper.toEntity(credentials));

        repository.save(entity);
    }

    @Override
    public Optional<Credentials> findByUsername(String username) {
        return repository.findByUsername(username)
            .map(CredentialsMapper::toDomain);
    }
}
