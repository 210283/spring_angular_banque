package com.votrebanque.infrastructure.persistence.entity;

import com.votrebanque.domain.model.DirectDebitFrequency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "direct_debits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DirectDebitEntity {

    @Id
    private String id;

    @Column(name = "source_account_number", nullable = false)
    private String sourceAccountNumber;

    @Column(name = "beneficiary_account_number", nullable = false)
    private String beneficiaryAccountNumber;

    @Column(name = "beneficiary_label")
    private String beneficiaryLabel;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DirectDebitFrequency frequency;

    @Column(name = "next_execution_date", nullable = false)
    private LocalDate nextExecutionDate;

    @Column(nullable = false)
    private boolean active;

    @Version
    private Long version;
}
