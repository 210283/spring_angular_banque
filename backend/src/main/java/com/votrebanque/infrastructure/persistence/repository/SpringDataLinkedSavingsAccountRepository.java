package com.votrebanque.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.votrebanque.infrastructure.persistence.entity.LinkedSavingsAccountEntity;

public interface SpringDataLinkedSavingsAccountRepository extends JpaRepository<LinkedSavingsAccountEntity, String> {
    boolean existsByCurrentAccountNumberAndSavingsAccountNumber(String currentAccountNumber, String savingsAccountNumber);
    List<LinkedSavingsAccountEntity> findAllByCurrentAccountNumber(String currentAccountNumber);
}
