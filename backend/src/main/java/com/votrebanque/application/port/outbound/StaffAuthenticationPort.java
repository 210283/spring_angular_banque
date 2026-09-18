package com.votrebanque.application.port.outbound;

import java.util.Optional;

public interface StaffAuthenticationPort {
    Optional<StaffUser> authenticateStaff(String username, String rawPassword);

    record StaffUser(String username, String role) {}
}
