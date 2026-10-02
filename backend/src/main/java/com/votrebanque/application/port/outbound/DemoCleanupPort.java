package com.votrebanque.application.port.outbound;

import java.util.List;

public interface DemoCleanupPort {
    void deleteAccountsAndCredentials(List<String> accountNumbers, List<String> usernames);
}
