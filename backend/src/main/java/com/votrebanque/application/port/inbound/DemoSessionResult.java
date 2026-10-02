package com.votrebanque.application.port.inbound;

import java.time.Instant;

public record DemoSessionResult(String token, String mainAccountNumber, String secondaryAccountNumber, Instant expiresAt) {
}
