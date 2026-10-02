package com.votrebanque.infrastructure.persistence.adapter;

import java.time.Instant;
import java.util.List;

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
            demoSession.getUsername(),
            demoSession.getAccountNumbers(),
            demoSession.getCreatedAt(),
            demoSession.getExpiresAt()
        ));
    }

    @Override
    public List<DemoSession> findExpired(Instant now) {
        return repository.findAllByExpiresAtBefore(now).stream()
            .map(entity -> DemoSession.reconstruct(
                entity.getId(), entity.getUsername(), entity.getAccountNumbers(),
                entity.getCreatedAt(), entity.getExpiresAt()
            ))
            .toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
