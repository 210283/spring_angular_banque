package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.application.port.outbound.ActivationTokenRepositoryPort;
import com.votrebanque.domain.model.ActivationToken;
import com.votrebanque.infrastructure.persistence.entity.ActivationTokenEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataActivationTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ActivationTokenPersistenceAdapter implements ActivationTokenRepositoryPort {

    private final SpringDataActivationTokenRepository repository;

    public ActivationTokenPersistenceAdapter(SpringDataActivationTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(ActivationToken token) {
        Optional<ActivationTokenEntity> existingEntity = repository.findById(token.id());

        ActivationTokenEntity entity;
        if (existingEntity.isPresent()) {
            entity = ActivationTokenMapper.updateEntity(existingEntity.get(), token);
        } else {
            entity = ActivationTokenMapper.toEntity(token);
        }

        repository.save(entity);
    }

    @Override
    public Optional<ActivationToken> findByUsername(String username) {
        return repository.findByUsernameOrderByExpiresAtDesc(username).stream()
            .map(ActivationTokenMapper::toDomain)
            .filter(ActivationToken::isValid)
            .findFirst();
    }

    @Override
    public Optional<ActivationToken> findById(String id) {
        return repository.findById(id)
            .map(ActivationTokenMapper::toDomain);
    }
}