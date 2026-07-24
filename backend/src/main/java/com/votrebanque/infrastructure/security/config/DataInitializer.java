package com.votrebanque.infrastructure.security.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("test")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            System.out.println("====== TEST DATA INITIALIZATION ======");
            
            // Preventative cleaning
            jdbcTemplate.execute("TRUNCATE TABLE bank_accounts CASCADE;");
            
            // Insert your test accounts
            jdbcTemplate.execute("INSERT INTO bank_accounts (account_number, balance, owner) VALUES ('FR7612345', 1000.00, 'Armand');");
            jdbcTemplate.execute("INSERT INTO bank_accounts (account_number, balance, owner) VALUES ('FR7698765', 500.00, 'Destinataire');");
            
            System.out.println("====== INITIALIZATION COMPLETE ======");
        };
    }
}