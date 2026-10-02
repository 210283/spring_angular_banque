package com.votrebanque.infrastructure.adapters.inbound.rest.response;

import java.time.Instant;

public record DemoSessionResponse(String token, String mainAccountNumber, String secondaryAccountNumber, Instant expiresAt) {
}
