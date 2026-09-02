package com.votrebanque.domain.model;

import java.math.BigDecimal;
import java.util.Optional;

public enum AccountType {
    CURRENT("Current account", BigDecimal.ZERO, null),
    SAVINGS("Savings account", new BigDecimal("0.015"), null),           // 1.5% indicative
    BOOKLET("A booklet", new BigDecimal("0.03"), Money.from(22950)),     // 3% + regulatory ceiling
    LDD("LDD", new BigDecimal("0.03"), Money.from(12000));               // 3% + regulatory ceiling

    private final String label;
    private final BigDecimal defaultAnnualRate;
    private final Money depositCap;

    AccountType(String label, BigDecimal defaultAnnualRate, Money depositCap) {
        this.label = label;
        this.defaultAnnualRate = defaultAnnualRate;
        this.depositCap = depositCap;
    }

    public String label() {
        return label;
    }

    public BigDecimal defaultAnnualRate() {
        return defaultAnnualRate;
    }

    public Optional<Money> depositCap() {
        return Optional.ofNullable(depositCap);
    }

    public boolean isSavings() {
        return this != CURRENT;
    }
}