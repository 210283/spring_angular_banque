package com.votrebanque.infrastructure.persistence.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votrebanque.infrastructure.persistence.entity.DemoSessionEntity;

public interface SpringDataDemoSessionRepository extends JpaRepository<DemoSessionEntity, String> {
    List<DemoSessionEntity> findAllByExpiresAtBefore(Instant now);
}
