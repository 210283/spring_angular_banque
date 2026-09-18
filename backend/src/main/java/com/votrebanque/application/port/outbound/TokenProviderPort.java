package com.votrebanque.application.port.outbound;

public interface TokenProviderPort {
    String generateToken(String username, String role);
    String extractUsername(String token);
    String extractRole(String token);
    boolean isTokenValid(String token);
}
