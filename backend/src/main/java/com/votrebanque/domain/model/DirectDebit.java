package com.votrebanque.domain.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class DirectDebit extends AbstractEntity<String> {

    private final String id;
    private final AccountId sourceAccountId;
    private final AccountId beneficiaryAccountId;
    private final String beneficiaryLabel;
    private final Money amount;
    private final DirectDebitFrequency frequency;
    private LocalDate nextExecutionDate;
    private boolean active;

    private DirectDebit(String id, AccountId sourceAccountId, AccountId beneficiaryAccountId, String beneficiaryLabel,
                         Money amount, DirectDebitFrequency frequency, LocalDate nextExecutionDate, boolean active) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.beneficiaryAccountId = beneficiaryAccountId;
        this.beneficiaryLabel = beneficiaryLabel;
        this.amount = amount;
        this.frequency = frequency;
        this.nextExecutionDate = nextExecutionDate;
        this.active = active;
    }

    public static DirectDebit create(AccountId sourceAccountId, AccountId beneficiaryAccountId, String beneficiaryLabel,
                                      Money amount, DirectDebitFrequency frequency, LocalDate startDate) {
        Objects.requireNonNull(sourceAccountId, "sourceAccountId can't be null");
        Objects.requireNonNull(beneficiaryAccountId, "beneficiaryAccountId can't be null");
        Objects.requireNonNull(amount, "amount can't be null");
        Objects.requireNonNull(frequency, "frequency can't be null");
        Objects.requireNonNull(startDate, "startDate can't be null");

        if (sourceAccountId.equals(beneficiaryAccountId)) {
            throw new IllegalArgumentException("Cannot set up a direct debit to your own account.");
        }
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("The direct debit amount must be positive.");
        }

        return new DirectDebit(UUID.randomUUID().toString(), sourceAccountId, beneficiaryAccountId, beneficiaryLabel,
            amount, frequency, startDate, true);
    }

    public static DirectDebit reconstruct(String id, AccountId sourceAccountId, AccountId beneficiaryAccountId,
                                           String beneficiaryLabel, Money amount, DirectDebitFrequency frequency,
                                           LocalDate nextExecutionDate, boolean active) {
        return new DirectDebit(id, sourceAccountId, beneficiaryAccountId, beneficiaryLabel, amount, frequency,
            nextExecutionDate, active);
    }

    public boolean isDueOn(LocalDate date) {
        return active && !nextExecutionDate.isAfter(date);
    }

    public void advanceToNextExecution() {
        this.nextExecutionDate = switch (frequency) {
            case WEEKLY -> nextExecutionDate.plusWeeks(1);
            case MONTHLY -> nextExecutionDate.plusMonths(1);
        };
    }

    public void cancel() {
        this.active = false;
    }

    public String id() { return id; }
    public AccountId sourceAccountId() { return sourceAccountId; }
    public AccountId beneficiaryAccountId() { return beneficiaryAccountId; }
    public String beneficiaryLabel() { return beneficiaryLabel; }
    public Money amount() { return amount; }
    public DirectDebitFrequency frequency() { return frequency; }
    public LocalDate nextExecutionDate() { return nextExecutionDate; }
    public boolean active() { return active; }
}
