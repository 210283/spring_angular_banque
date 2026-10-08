package com.votrebanque.infrastructure.persistence.adapter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.votrebanque.application.port.outbound.DemoSessionRepositoryPort;
import com.votrebanque.domain.model.DemoSession;
import com.votrebanque.infrastructure.persistence.entity.DemoSessionEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataDemoSessionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DemoSessionPersistenceAdapter implements DemoSessionRepositoryPort {

    private final SpringDataDemoSessionRepository repository;

    @Override
    public void save(DemoSession demoSession) {
        repository.save(new DemoSessionEntity(
            demoSession.id(),
            demoSession.getUsernames(),
            demoSession.getAccountNumbers(),
            demoSession.getCreatedAt(),
            demoSession.getExpiresAt()
        ));
    }

    @Override
    public List<DemoSession> findExpired(Instant now) {
        return repository.findAllByExpiresAtBefore(now).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public Optional<DemoSession> findByUsername(String username) {
        return repository.findByUsername(username).map(this::toDomain);
    }

    private DemoSession toDomain(DemoSessionEntity entity) {
        return DemoSession.reconstruct(
            entity.getId(), entity.getUsernames(), entity.getAccountNumbers(),
            entity.getCreatedAt(), entity.getExpiresAt()
        );
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
