package com.votrebanque.infrastructure.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.votrebanque.application.port.outbound.EmailSenderPort;

@Component
public class SmtpEmailAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;
    private final String frontendBaseUrl;

    public SmtpEmailAdapter(JavaMailSender mailSender, @Value("${app.frontend.base-url}") String frontendBaseUrl) {
        this.mailSender = mailSender;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    public void sendActivationEmail(String username, String rawToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(username + "@votrebanque.com");
        message.setFrom("noreply@votrebanque.com");
        message.setSubject("Activate your bank account");

        String activationUrl = String.format(
            "%s/activate?user=%s&token=%s", frontendBaseUrl, username, rawToken
        );
        message.setText("Welcome! Please activate your account by clicking the following link:\n" 
                + activationUrl + "\n");

        mailSender.send(message);
    }
}
