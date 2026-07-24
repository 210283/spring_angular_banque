package com.votrebanque.domain.model;

import java.security.SecureRandom;

public final class UsernameGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int LENGTH = 11;

    private UsernameGenerator() {}

    public static String generate() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(RANDOM.nextInt(10)); // chiffre entre 0 et 9
        }
        return sb.toString();
    }
}
