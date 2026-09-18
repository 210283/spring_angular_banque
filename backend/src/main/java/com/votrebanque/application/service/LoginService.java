package com.votrebanque.application.service;

import java.util.Optional;


import com.votrebanque.application.port.inbound.LoginUseCase;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.application.port.outbound.PasswordEncoderPort;
import com.votrebanque.application.port.outbound.StaffAuthenticationPort;
import com.votrebanque.application.port.outbound.TokenProviderPort;
import com.votrebanque.domain.exception.InvalidCredentialsException;
import com.votrebanque.domain.model.Credentials;

public class LoginService implements LoginUseCase {

    private final CredentialsRepositoryPort credentialsRepository;
    private final StaffAuthenticationPort staffAuthenticationPort;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public LoginService(CredentialsRepositoryPort credentialsRepository,
                        StaffAuthenticationPort staffAuthenticationPort,  
                        PasswordEncoderPort passwordEncoder,
                        TokenProviderPort tokenProvider) {
        this.credentialsRepository = credentialsRepository;
        this.staffAuthenticationPort = staffAuthenticationPort;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String login(String username, String rawPassword) {
        // attempt authentication admin/staff user first
        Optional<StaffAuthenticationPort.StaffUser> staffUser =
            staffAuthenticationPort.authenticateStaff(username, rawPassword);

        if (staffUser.isPresent()) {
            return tokenProvider.generateToken(username, staffUser.get().role());
        }

        Credentials credentials = credentialsRepository.findByUsername(username)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        boolean success = credentials.attemptLogin(rawPassword, passwordEncoder::matches);

        // Persists the updated state (failure counter, potential lockout)
        credentialsRepository.save(credentials);

        if (!success) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return tokenProvider.generateToken(username, "ROLE_CLIENT");
    }
}
