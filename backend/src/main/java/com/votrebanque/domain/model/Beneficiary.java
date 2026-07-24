package com.votrebanque.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Beneficiary extends AbstractEntity<String> {

    private final String id;
    private final String label;
    private final AccountId accountId;
    private final AccountId ownerId;

    private Beneficiary(String id, String label, AccountId accountId, AccountId ownerId) {
        Objects.requireNonNull(id, "Beneficiary id is required");
        Objects.requireNonNull(label, "Beneficiary label is required");
        Objects.requireNonNull(accountId, "Target account id is required");
        Objects.requireNonNull(ownerId, "Owner account id is required");

        if (id.isBlank()) {
            throw new IllegalArgumentException("Beneficiary id cannot be blank");
        }
        if (label.isBlank()) {
            throw new IllegalArgumentException("Beneficiary label cannot be blank");
        }
        if (accountId.equals(ownerId)) {
            throw new IllegalArgumentException("Cannot add your own account as a beneficiary");
        }

        this.id = id;
        this.label = label;
        this.accountId = accountId;
        this.ownerId = ownerId;
    }

    public static Beneficiary add(String label, AccountId accountId, AccountId ownerId) {
        return new Beneficiary(UUID.randomUUID().toString(), label, accountId, ownerId);
    }

    public static Beneficiary reconstruct(String id, String label, AccountId accountId, AccountId ownerId) {
        return new Beneficiary(id, label, accountId, ownerId);
    }

    public String label() {
        return label;
    }

    public AccountId accountId() {
        return accountId;
    }

    public AccountId ownerId() {
        return ownerId;
    }

    @Override
    public String id() {
        return id;
    }
}