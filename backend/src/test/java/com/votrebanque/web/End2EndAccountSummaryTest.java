package com.votrebanque.web;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;

import com.votrebanque.infrastructure.persistence.entity.AccountEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataAccountRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@WithMockUser(username = "admin", roles = "ADMIN")
public class End2EndAccountSummaryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataAccountRepository jpaRepository;

    @BeforeEach
    void setup() {
        AccountEntity account = new AccountEntity("FR761234567", "Charlie", BigDecimal.valueOf(1234.56), null);
        jpaRepository.save(account);
    }

    @Test
    void shouldReturnAccountSummaryForExistingAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/FR761234567/summary")
                //.with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password123"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"accountId\":\"FR761234567\"," +
                        "\"owner\":\"Charlie\"," +
                        "\"balance\":1234.56" +
                        "}"));
    }

    @Test
    void shouldReturnNotFoundForMissingAccountSummary() throws Exception {
        mockMvc.perform(get("/api/accounts/FR760000000/summary")
                //.with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password123"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
