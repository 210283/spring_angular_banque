package com.votrebanque.application.port.outbound;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.votrebanque.domain.model.DemoSession;

public interface DemoSessionRepositoryPort {
    void save(DemoSession demoSession);
    List<DemoSession> findExpired(Instant now);
    Optional<DemoSession> findByUsername(String username);
    void deleteById(String id);
}
