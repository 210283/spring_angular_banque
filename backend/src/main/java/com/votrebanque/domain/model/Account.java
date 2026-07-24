package com.votrebanque.domain.model;

import java.util.Objects;

import com.votrebanque.domain.exception.InsufficientFundsException;

public class Account extends AbstractEntity<AccountId>{

    private static final Money MINIMUM_OPENING_DEPOSIT = Money.from(20);

    private final AccountId accountNumber;
    private final String owner;
    private Money balance;

    private Account(AccountId accountNumber, String owner, Money balance) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = balance;
    }

    // open account: initial deposit must be more than 20
    public static Account open(AccountId accountNumber, String owner, Money initialDeposit) {
        Objects.requireNonNull(accountNumber, "The account number can't be null");
        Objects.requireNonNull(owner, "The owner can't be null");
        Objects.requireNonNull(initialDeposit, "The initial deposit can't be null");

        if (!initialDeposit.isGreaterThan(MINIMUM_OPENING_DEPOSIT)) {
            throw new IllegalArgumentException(
                "The initial deposit must be greater than " + MINIMUM_OPENING_DEPOSIT.amount());
        }
        return new Account(accountNumber, owner, initialDeposit);
    }

    public static Account reconstruct(AccountId accountNumber, String owner, Money balance) {
        Objects.requireNonNull(accountNumber, "The account number can't be null");
        Objects.requireNonNull(owner, "The owner can't be null");
        Objects.requireNonNull(balance, "The balance can't be null");
        return new Account(accountNumber, owner, balance);
    }

    // Business rule: Debit and credit money
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

    public AccountId accountNumber() { return accountNumber; }
    public String owner() { return owner; }
    public Money balance() { return balance; }

    // fingerprint
    @Override
    public AccountId id() {
        return accountNumber;
    }
}