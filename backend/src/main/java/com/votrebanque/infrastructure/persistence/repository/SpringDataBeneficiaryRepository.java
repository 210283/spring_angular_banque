package com.votrebanque.infrastructure.persistence.repository;

import com.votrebanque.infrastructure.persistence.entity.BeneficiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataBeneficiaryRepository extends JpaRepository<BeneficiaryEntity, String> {
    //@Query("select b from BeneficiaryEntity b join b.accounts a where a.accountNumber = :accountNumber")
    List<BeneficiaryEntity> findByOwnerAccountNumber(String accountNumber);

    Optional<BeneficiaryEntity> findByTargetAccountNumberAndOwnerAccountNumber(String targetAccountNumber, String OwnerAccountNumber);
}
