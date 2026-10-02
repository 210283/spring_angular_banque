package com.votrebanque.infrastructure.persistence.adapter;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.votrebanque.application.port.outbound.DemoCleanupPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JdbcDemoCleanupAdapter implements DemoCleanupPort {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void deleteAccountsAndCredentials(List<String> accountNumbers, List<String> usernames) {
        if (accountNumbers.isEmpty()) {
            return;
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("accountNumbers", accountNumbers)
            .addValue("usernames", usernames);

        jdbcTemplate.update(
            "DELETE FROM direct_debits WHERE source_account_number IN (:accountNumbers)", params);
        jdbcTemplate.update(
            "DELETE FROM transactions WHERE account_number IN (:accountNumbers)", params);
        jdbcTemplate.update(
            "DELETE FROM beneficiaries WHERE owner_account_number IN (:accountNumbers) OR target_account_number IN (:accountNumbers)",
            params);
        jdbcTemplate.update(
            "DELETE FROM linked_savings_accounts WHERE current_account_number IN (:accountNumbers) OR savings_account_number IN (:accountNumbers)",
            params);
        jdbcTemplate.update(
            "DELETE FROM activation_tokens WHERE username IN (:usernames)", params);
        jdbcTemplate.update(
            "DELETE FROM credentials WHERE username IN (:usernames)", params);
        jdbcTemplate.update(
            "DELETE FROM accounts WHERE account_number IN (:accountNumbers)", params);
    }
}
