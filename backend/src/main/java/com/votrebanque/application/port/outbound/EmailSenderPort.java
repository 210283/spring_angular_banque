package com.votrebanque.application.port.outbound;

public interface EmailSenderPort {
    void sendActivationEmail(String username, String activationUrl);
}
