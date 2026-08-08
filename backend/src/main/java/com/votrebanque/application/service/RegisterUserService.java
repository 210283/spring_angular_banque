package com.votrebanque.application.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.votrebanque.application.port.inbound.RegisterUserUseCase;
import com.votrebanque.application.port.outbound.ActivationTokenRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.port.outbound.EmailSenderPort;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.ActivationToken;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.domain.model.UsernameGenerator;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private static final int MAX_GENERATION_ATTEMPTS = 10;
    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);
    private static final Duration TOKEN_VALIDITY = Duration.ofHours(24);

    private final CredentialsRepositoryPort credentialsRepository;
    private final ActivationTokenRepositoryPort tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderPort emailSender;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;
    
    public RegisterUserService(CredentialsRepositoryPort credentialsRepository,
                                ActivationTokenRepositoryPort tokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailSenderPort emailSender) {
        this.credentialsRepository = credentialsRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
    }

    @Override
    public RegistrationResult registerUser(AccountId accountId) {
        String username = generateUniqueUsername();

        String temporaryPassword = generateSecureRandomToken();
        Credentials credentials = Credentials.register(username, accountId, passwordEncoder.encode(temporaryPassword));
        credentialsRepository.save(credentials);

        String rawToken = generateSecureRandomToken();
        ActivationToken token = ActivationToken.generate(
            username,
            passwordEncoder.encode(rawToken),
            TOKEN_VALIDITY
        );
        tokenRepository.save(token);

        String activationUrl = String.format("%s/activate?user=%s&token=%s", frontendBaseUrl, username, rawToken);
        
        try {
            emailSender.sendActivationEmail(username, activationUrl);
            token.markEmailSent();
        } catch (Exception e) {
            log.error("Failed to send activation email to {}", username, e);
        }
        tokenRepository.save(token);
        
        return new RegistrationResult(username, activationUrl);
    }

    private String generateUniqueUsername() {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String candidate = UsernameGenerator.generate();
            if (credentialsRepository.findByUsername(candidate).isEmpty()) {
                return candidate;
            }
        }

        // Extremely unlikely with 11 digits (10^11 combinations), but we refuse to loop indefinitely
        log.error("Failed to generate a unique username after {} attempts", MAX_GENERATION_ATTEMPTS);
        throw new IllegalStateException(
            "Unable to generate a unique customer ID after " + MAX_GENERATION_ATTEMPTS + " attempts"
        );
    }

    private String generateSecureRandomToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
