package com.votrebanque.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import com.votrebanque.domain.exception.InsufficientFundsException;

public class Account extends AbstractEntity<AccountId> {

    private static final Money MINIMUM_OPENING_DEPOSIT = Money.from(20);

    private final AccountId accountNumber;
    private final String owner;
    private final AccountType accountType;
    private final BigDecimal interestRate;
    private Money balance;
    private LocalDate lastInterestAccrualDate;

    private Account(AccountId accountNumber, String owner, Money balance,
                     AccountType accountType, BigDecimal interestRate, LocalDate lastInterestAccrualDate) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = balance;
        this.accountType = accountType;
        this.interestRate = interestRate;
        this.lastInterestAccrualDate = lastInterestAccrualDate;
    }

    // Current account
    public static Account open(AccountId accountNumber, String owner, Money initialDeposit) {
        return openWithType(accountNumber, owner, initialDeposit, AccountType.CURRENT);
    }

    // Savings account / A booklet / LDD
    public static Account openSavings(AccountId accountNumber, String owner, Money initialDeposit, AccountType accountType) {
        if (accountType == AccountType.CURRENT) {
            throw new IllegalArgumentException("Use open(...) for checking accounts.");
        }
        return openWithType(accountNumber, owner, initialDeposit, accountType);
    }

    private static Account openWithType(AccountId accountNumber, String owner, Money initialDeposit, AccountType accountType) {
        Objects.requireNonNull(accountNumber, "The account number can't be null");
        Objects.requireNonNull(owner, "The owner can't be null");
        Objects.requireNonNull(initialDeposit, "The initial deposit can't be null");
        Objects.requireNonNull(accountType, "The account type can't be null");

        if (!initialDeposit.isGreaterThan(MINIMUM_OPENING_DEPOSIT)) {
            throw new IllegalArgumentException(
                "The initial deposit must be greater than " + MINIMUM_OPENING_DEPOSIT.amount());
        }

        accountType.depositCap().ifPresent(cap -> {
            if (initialDeposit.isGreaterThan(cap)) {
                throw new IllegalArgumentException(
                    "The initial deposit exceeds the maximum allowed for " + accountType.label()
                        + " (" + cap.amount() + ")");
            }
        });

        return new Account(accountNumber, owner, initialDeposit, accountType, accountType.defaultAnnualRate(), LocalDate.now());
    }

    public static Account reconstruct(AccountId accountNumber, String owner, Money balance,
                                       AccountType accountType, BigDecimal interestRate, LocalDate lastInterestAccrualDate) {
        Objects.requireNonNull(accountNumber, "The account number can't be null");
        Objects.requireNonNull(owner, "The owner can't be null");
        Objects.requireNonNull(balance, "The balance can't be null");
        Objects.requireNonNull(accountType, "The account type can't be null");
        return new Account(accountNumber, owner, balance, accountType, interestRate, lastInterestAccrualDate);
    }

    public void debit(Money amount) {
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("The debit amount must be positive.");
        }
        if (this.balance.isLessThan(amount)) {
            throw new InsufficientFundsException("Insufficient funds to complete the transfer.");
        }
        this.balance = this.balance.less(amount);
    }

    public void credit(Money amount) {
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("The amount of the credit must be positive.");
        }
        this.balance = this.balance.more(amount);
    }

    // Actual interest accrual: simple interest for the period, applied to the current balance.
    // Called regularly (nightly job), the compounding occurs naturally from one call to the next.
    public void accrueInterest(LocalDate asOf) {
        if (!accountType.isSavings() || interestRate.compareTo(BigDecimal.ZERO) == 0) {
            this.lastInterestAccrualDate = asOf;
            return;
        }

        long daysElapsed = ChronoUnit.DAYS.between(lastInterestAccrualDate, asOf);
        if (daysElapsed <= 0) {
            return;
        }

        BigDecimal dailyRate = interestRate.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_EVEN);
        BigDecimal periodRate = dailyRate.multiply(BigDecimal.valueOf(daysElapsed));
        Money interest = new Money(this.balance.amount().multiply(periodRate));

        if (!interest.isNegativeOrZero()) {
            this.balance = this.balance.more(interest);
        }
        this.lastInterestAccrualDate = asOf;
    }

    public AccountId accountNumber() { return accountNumber; }
    public String owner() { return owner; }
    public Money balance() { return balance; }
    public AccountType accountType() { return accountType; }
    public BigDecimal interestRate() { return interestRate; }
    public LocalDate lastInterestAccrualDate() { return lastInterestAccrualDate; }

    @Override
    public AccountId id() {
        return accountNumber;
    }
}