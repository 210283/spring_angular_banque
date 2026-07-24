package com.votrebanque.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "beneficiaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String label;

    @Column(name = "target_account_number", nullable = false)
    private String targetAccountNumber;

    @Column(name = "owner_account_number", nullable = false)
    private String ownerAccountNumber;

    @Version
    private Long version;
}
