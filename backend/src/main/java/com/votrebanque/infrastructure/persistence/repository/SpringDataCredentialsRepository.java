package com.votrebanque.infrastructure.persistence.repository;

import com.votrebanque.infrastructure.persistence.entity.CredentialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataCredentialsRepository extends JpaRepository<CredentialsEntity, String> {
    Optional<CredentialsEntity> findByUsername(String username);
}
