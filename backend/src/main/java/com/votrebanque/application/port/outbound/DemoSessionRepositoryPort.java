package com.votrebanque.application.port.outbound;

import java.time.Instant;
import java.util.List;

import com.votrebanque.domain.model.DemoSession;

public interface DemoSessionRepositoryPort {
    void save(DemoSession demoSession);
    List<DemoSession> findExpired(Instant now);
    void deleteById(String id);
}
