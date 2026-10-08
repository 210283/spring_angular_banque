package com.votrebanque.application.port.inbound;

import java.time.Instant;

public record DemoSessionResult(String clientToken, String adminToken, String mainAccountNumber, String secondaryAccountNumber, Instant expiresAt) {
}
