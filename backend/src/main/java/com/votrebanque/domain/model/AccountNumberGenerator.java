package com.votrebanque.domain.model;

import java.util.concurrent.ThreadLocalRandom;

public final class AccountNumberGenerator {

    private AccountNumberGenerator() {
    }

    public static AccountId generate() {
        int randomDigits = ThreadLocalRandom.current().nextInt(1_000_000, 10_000_000);
        return new AccountId("FR76" + randomDigits);
    }
}
