package com.votrebanque.web; // À adapter selon votre structure de package

import com.votrebanque.infrastructure.persistence.repository.SpringDataAccountRepository;
import com.votrebanque.infrastructure.persistence.entity.AccountEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataBeneficiaryRepository;
import com.votrebanque.infrastructure.persistence.entity.BeneficiaryEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@WithMockUser(username = "admin", roles = "ADMIN")
public class End2EndGetBeneficiariesTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataAccountRepository accountRepository;

    @Autowired
    private SpringDataBeneficiaryRepository beneficiaryRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private static final String ACCOUNT_NUMBER = "FR761234567";

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();
        beneficiaryRepository.deleteAll();

        AccountEntity account = new AccountEntity(ACCOUNT_NUMBER, "Charlie", BigDecimal.valueOf(1234.56), null);
        accountRepository.save(account);

        BeneficiaryEntity b1 = new BeneficiaryEntity(UUID.randomUUID().toString(), "b-id-1", "FR769876567", ACCOUNT_NUMBER, null);
        BeneficiaryEntity b2 = new BeneficiaryEntity(UUID.randomUUID().toString(), "b-id-2", "FR761112223", ACCOUNT_NUMBER, null);

        beneficiaryRepository.save(b1);
        beneficiaryRepository.save(b2);
        
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldReturnBeneficiariesListForExistingAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/" + ACCOUNT_NUMBER + "/beneficiaries")
                //.with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password123"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[" +
                        "{" +
                        "\"label\":\"b-id-1\"," +
                        "\"beneficiaryAccountNumber\":\"FR769876567\"" +
                        "}," +
                        "{" +
                        "\"label\":\"b-id-2\"," +
                        "\"beneficiaryAccountNumber\":\"FR761112223\"" +
                        "}" +
                        "]"));
    }

    @Test
    void shouldReturnEmptyListWhenAccountHasNoBeneficiaries() throws Exception {
        String emptyAccountStr = "FR760000000";
        
        AccountEntity emptyAccount = new AccountEntity(emptyAccountStr, "John Doe", BigDecimal.valueOf(100.00), null);
        accountRepository.save(emptyAccount);

        mockMvc.perform(get("/api/accounts/" + emptyAccountStr + "/beneficiaries")
                //.with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password123"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}