package com.votrebanque.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "linked_savings_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkedSavingsAccountEntity {

    @Id
    @Column(name = "savings_account_number", nullable = false)
    private String savingsAccountNumber;

    @Column(name = "current_account_number", nullable = false)
    private String currentAccountNumber;
}
