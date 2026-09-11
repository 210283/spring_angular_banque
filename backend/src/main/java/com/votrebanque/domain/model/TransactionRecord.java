package com.votrebanque.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TransactionRecord extends AbstractEntity<String> {

    private final String id;
    private final AccountId accountNumber;
    private final TransactionType type;
    private final TransactionCategory category;
    private final Money amount;
    private final Money balanceAfter;
    private final AccountId counterpartyAccountNumber; // nullable (ex: initial deposit)
    private final String counterpartyName;              // nullable
    private final String label;
    private final Instant occurredAt;
    private final String correlationId;                 // links the two lines of the same transfer

    private TransactionRecord(String id, AccountId accountNumber, TransactionType type, TransactionCategory category,
                               Money amount, Money balanceAfter, AccountId counterpartyAccountNumber,
                               String counterpartyName, String label, Instant occurredAt, String correlationId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.counterpartyAccountNumber = counterpartyAccountNumber;
        this.counterpartyName = counterpartyName;
        this.label = label;
        this.occurredAt = occurredAt;
        this.correlationId = correlationId;
    }

    public static TransactionRecord record(AccountId accountNumber, TransactionType type, TransactionCategory category,
                                            Money amount, Money balanceAfter, AccountId counterpartyAccountNumber,
                                            String counterpartyName, String label, String correlationId) {
        Objects.requireNonNull(accountNumber, "accountNumber can't be null");
        Objects.requireNonNull(type, "type can't be null");
        Objects.requireNonNull(category, "category can't be null");
        Objects.requireNonNull(amount, "amount can't be null");
        Objects.requireNonNull(balanceAfter, "balanceAfter can't be null");

        return new TransactionRecord(UUID.randomUUID().toString(), accountNumber, type, category, amount,
            balanceAfter, counterpartyAccountNumber, counterpartyName, label, Instant.now(), correlationId);
    }

    public static TransactionRecord reconstruct(String id, AccountId accountNumber, TransactionType type,
                                                  TransactionCategory category, Money amount, Money balanceAfter,
                                                  AccountId counterpartyAccountNumber, String counterpartyName,
                                                  String label, Instant occurredAt, String correlationId) {
        return new TransactionRecord(id, accountNumber, type, category, amount, balanceAfter,
            counterpartyAccountNumber, counterpartyName, label, occurredAt, correlationId);
    }

    public String id() { return id; }
    public AccountId accountNumber() { return accountNumber; }
    public TransactionType type() { return type; }
    public TransactionCategory category() { return category; }
    public Money amount() { return amount; }
    public Money balanceAfter() { return balanceAfter; }
    public AccountId counterpartyAccountNumber() { return counterpartyAccountNumber; }
    public String counterpartyName() { return counterpartyName; }
    public String label() { return label; }
    public Instant occurredAt() { return occurredAt; }
    public String correlationId() { return correlationId; }
}