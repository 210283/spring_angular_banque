package com.votrebanque.infrastructure.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.votrebanque.application.port.outbound.EmailSenderPort;

@Component
public class SmtpEmailAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    public SmtpEmailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendActivationEmail(String username, String activationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(username + "@votrebanque.com");
        message.setFrom("noreply@votrebanque.com");
        message.setSubject("Activate your bank account");

        String body = String.format("""
            Welcome to VotreBanque!

            Your login ID is: %s

            To activate your account and choose your password, click the following link:
            %s

            This link is valid for 24 hours.
            """, username, activationUrl);

        message.setText(body);
        mailSender.send(message);
    }
}
