package com.votrebanque.infrastructure.security.adapter;

import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.votrebanque.application.port.outbound.StaffAuthenticationPort;
import com.votrebanque.domain.exception.InvalidCredentialsException;

@Component
public class SpringSecurityStaffAuthenticationAdapter implements StaffAuthenticationPort {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SpringSecurityStaffAuthenticationAdapter(UserDetailsService userDetailsService,
                                                   PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<StaffUser> authenticateStaff(String username, String rawPassword) {
        try {
            UserDetails staffUser = userDetailsService.loadUserByUsername(username);

            if (!passwordEncoder.matches(rawPassword, staffUser.getPassword())) {
                throw new InvalidCredentialsException("Invalid username or password");
            }

            String role = staffUser.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_ADMIN");

            return Optional.of(new StaffUser(staffUser.getUsername(), role));

        } catch (UsernameNotFoundException notStaff) {
            return Optional.empty();
        }
    }
}
