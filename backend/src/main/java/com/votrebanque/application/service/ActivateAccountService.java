package com.votrebanque.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.votrebanque.application.port.inbound.ActivateAccountUseCase;
import com.votrebanque.application.port.outbound.ActivationTokenRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.domain.exception.InvalidOrExpiredTokenException;
import com.votrebanque.domain.model.ActivationToken;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.domain.validator.PasswordPolicyValidator;

@Service
public class ActivateAccountService implements ActivateAccountUseCase {

    private final CredentialsRepositoryPort credentialsRepository;
    private final ActivationTokenRepositoryPort tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;

    public ActivateAccountService(CredentialsRepositoryPort credentialsRepository,
                                   ActivationTokenRepositoryPort tokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   PasswordPolicyValidator passwordPolicyValidator) {
        this.credentialsRepository = credentialsRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyValidator = passwordPolicyValidator;
    }

    @Override
    @Transactional 
    public void activateAccount(String username, String rawToken, String chosenPassword) {
        ActivationToken token = tokenRepository.findByUsername(username)
            .orElseThrow(() -> new InvalidOrExpiredTokenException("Token not found"));

        boolean activationSuccessful = token.attemptActivation(rawToken, passwordEncoder::matches);
        
        if (!activationSuccessful) {
            throw new InvalidOrExpiredTokenException("Invalid or expired token");
        }

        passwordPolicyValidator.validate(chosenPassword);

        Credentials credentials = credentialsRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalStateException("User not found"));

        credentials.changePassword(passwordEncoder.encode(chosenPassword));
        
        credentialsRepository.save(credentials);
        tokenRepository.save(token);
    }
}
