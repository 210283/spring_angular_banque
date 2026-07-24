package com.votrebanque.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) implements Comparable<Money> {

    public static final Money ZERO = new Money(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN));

    public Money {
        Objects.requireNonNull(amount, "The amount can't be null");
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static Money fromString(String value) {
        return new Money(new BigDecimal(value));
    }

    public static Money from(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public boolean isNegativeOrZero() {
        return this.amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isGreaterThan(Money other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public Money more(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money less(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    @Override
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }
}
