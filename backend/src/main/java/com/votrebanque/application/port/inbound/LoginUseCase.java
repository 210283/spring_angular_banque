package com.votrebanque.application.port.inbound;

public interface LoginUseCase {
    String login(String username, String rawPassword);
    // Returns the generated JWT on success; otherwise, raises a dedicated exception
}
