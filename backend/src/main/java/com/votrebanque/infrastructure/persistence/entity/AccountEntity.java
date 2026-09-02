package com.votrebanque.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.votrebanque.domain.model.AccountType;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    @Id
    @Column(name = "account_number")
    private String accountNumber;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, columnDefinition = "varchar(20) default 'CURRENT'")
    private AccountType accountType;

    @Column(name = "interest_rate", nullable = false, precision = 6, scale = 4, columnDefinition = "numeric(6,4) default 0")
    private BigDecimal interestRate;

    @Column(name = "last_interest_accrual_date", nullable = false, columnDefinition = "date default current_date")
    private LocalDate lastInterestAccrualDate;

    @Version
    private Long version;
}