package com.votrebanque.domain;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.votrebanque.domain.model.Money;

import static org.assertj.core.api.Assertions.*;

public class MoneyTest {

    @Test
    void shouldScaleAmountToTwoDecimalPlaces() {
        Money money = new Money(BigDecimal.valueOf(10));

        assertThat(money.amount()).isEqualByComparingTo(BigDecimal.valueOf(10.00).setScale(2));
    }

    @Test
    void shouldAddAndSubtractMoneyCorrectly() {
        Money first = new Money(BigDecimal.valueOf(100.00));
        Money second = new Money(BigDecimal.valueOf(25.50));

        Money sum = first.more(second);
        Money difference = first.less(second);

        assertThat(sum.amount()).isEqualByComparingTo(BigDecimal.valueOf(125.50).setScale(2));
        assertThat(difference.amount()).isEqualByComparingTo(BigDecimal.valueOf(74.50).setScale(2));
    }

    @Test
    void shouldDetectNegativeOrZeroValues() {
        assertThat(new Money(BigDecimal.ZERO).isNegativeOrZero()).isTrue();
        assertThat(new Money(BigDecimal.valueOf(-1.00)).isNegativeOrZero()).isTrue();
    }

    @Test
    void shouldFailWhenCreatingMoneyWithNullValue() {
        assertThatThrownBy(() -> new Money(null))
                .isInstanceOf(NullPointerException.class);
    }
}
