package com.votrebanque.infrastructure.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.votrebanque.infrastructure.persistence.entity.DemoSessionEntity;

public interface SpringDataDemoSessionRepository extends JpaRepository<DemoSessionEntity, String> {
    List<DemoSessionEntity> findAllByExpiresAtBefore(Instant now);

    @Query(value = "SELECT * FROM demo_sessions WHERE :username = ANY(usernames)", nativeQuery = true)
    Optional<DemoSessionEntity> findByUsername(@Param("username") String username);
}
