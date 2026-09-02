package com.votrebanque.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.domain.model.AccountType;
import com.votrebanque.infrastructure.persistence.entity.AccountEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataAccountRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
@WithMockUser(username = "admin", roles = "ADMIN")
public class End2EndAccountSummaryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataAccountRepository jpaRepository;

    @BeforeEach
    void setup() {
        AccountEntity account = new AccountEntity("FR761234567", "Charlie", BigDecimal.valueOf(1234.56), AccountType.CURRENT, BigDecimal.ZERO, LocalDate.now(), null);
        jpaRepository.save(account);
    }

    @Test
    void shouldReturnAccountSummaryForExistingAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/FR761234567/summary")
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
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
