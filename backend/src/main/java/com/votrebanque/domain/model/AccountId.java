package com.votrebanque.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

public record AccountId(String value) {

    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^FR76\\d{7}$");

    public AccountId {
        Objects.requireNonNull(value, "The account number cannot be null");


        if (!ACCOUNT_NUMBER_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid account number format. Must be FR76 followed by 7 digits.");
        }
    }
}
