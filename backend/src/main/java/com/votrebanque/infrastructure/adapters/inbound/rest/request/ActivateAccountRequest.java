package com.votrebanque.infrastructure.adapters.inbound.rest.request;

public record ActivateAccountRequest(String username, String token, String newPassword) {}
