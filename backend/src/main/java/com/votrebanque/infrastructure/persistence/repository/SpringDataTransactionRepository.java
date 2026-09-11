package com.votrebanque.infrastructure.persistence.repository;

import com.votrebanque.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, String> {
    List<TransactionEntity> findAllByAccountNumberOrderByOccurredAtDesc(String accountNumber);
}
