package com.votrebanque.application.service;

import com.votrebanque.application.port.inbound.LoginUseCase;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.domain.exception.InvalidCredentialsException;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.infrastructure.security.config.JwtTokenProvider;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginUseCase {

    private final CredentialsRepositoryPort credentialsRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginService(CredentialsRepositoryPort credentialsRepository,
                         UserDetailsService userDetailsService,  
                         PasswordEncoder passwordEncoder,
                         JwtTokenProvider jwtTokenProvider) {
        this.credentialsRepository = credentialsRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(String username, String rawPassword) {
        // attempt authentication admin/staff user first
        try {
            UserDetails staffUser = userDetailsService.loadUserByUsername(username);

            if (!passwordEncoder.matches(rawPassword, staffUser.getPassword())) {
                throw new InvalidCredentialsException("Invalid username or password");
            }

            String role = staffUser.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_ADMIN");

            return jwtTokenProvider.generateToken(username, role);

        } catch (UsernameNotFoundException notStaff) {
            // Not staff account : try classic client authentication
        }

        Credentials credentials = credentialsRepository.findByUsername(username)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        boolean success = credentials.attemptLogin(rawPassword, passwordEncoder::matches);

        // Persists the updated state (failure counter, potential lockout)
        credentialsRepository.save(credentials);

        if (!success) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return jwtTokenProvider.generateToken(username, "ROLE_CLIENT");
    }
}
