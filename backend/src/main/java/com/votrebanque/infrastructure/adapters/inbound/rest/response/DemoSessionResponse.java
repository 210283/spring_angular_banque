package com.votrebanque.infrastructure.adapters.inbound.rest.response;

import java.time.Instant;

public record DemoSessionResponse(String clientToken, String adminToken, String mainAccountNumber, String secondaryAccountNumber, Instant expiresAt) {
}
