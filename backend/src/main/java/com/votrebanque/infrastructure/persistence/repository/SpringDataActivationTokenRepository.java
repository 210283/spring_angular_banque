package com.votrebanque.infrastructure.persistence.repository;

import com.votrebanque.infrastructure.persistence.entity.ActivationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataActivationTokenRepository extends JpaRepository<ActivationTokenEntity, String> {
    List<ActivationTokenEntity> findByUsernameOrderByExpiresAtDesc(String username);
}
