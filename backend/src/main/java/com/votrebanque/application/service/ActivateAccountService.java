package com.votrebanque.application.service;


import com.votrebanque.application.port.inbound.ActivateAccountUseCase;
import com.votrebanque.application.port.outbound.ActivationTokenRepositoryPort;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.port.outbound.PasswordEncoderPort;
import com.votrebanque.domain.exception.InvalidOrExpiredTokenException;
import com.votrebanque.domain.model.ActivationToken;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.domain.validator.PasswordPolicyValidator;

public class ActivateAccountService implements ActivateAccountUseCase {

    private final CredentialsRepositoryPort credentialsRepository;
    private final ActivationTokenRepositoryPort tokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;

    public ActivateAccountService(CredentialsRepositoryPort credentialsRepository,
                                   ActivationTokenRepositoryPort tokenRepository,
                                   PasswordEncoderPort passwordEncoder,
                                   PasswordPolicyValidator passwordPolicyValidator) {
        this.credentialsRepository = credentialsRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyValidator = passwordPolicyValidator;
    }

    @Override
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
