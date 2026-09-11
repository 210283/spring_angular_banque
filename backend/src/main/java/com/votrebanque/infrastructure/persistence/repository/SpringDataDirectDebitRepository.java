package com.votrebanque.infrastructure.persistence.repository;

import com.votrebanque.infrastructure.persistence.entity.DirectDebitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface SpringDataDirectDebitRepository extends JpaRepository<DirectDebitEntity, String> {
    List<DirectDebitEntity> findAllBySourceAccountNumber(String sourceAccountNumber);
    List<DirectDebitEntity> findAllByActiveTrueAndNextExecutionDateLessThanEqual(LocalDate date);
}
