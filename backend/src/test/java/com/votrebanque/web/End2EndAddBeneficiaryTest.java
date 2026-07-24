package com.votrebanque.web;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.votrebanque.infrastructure.persistence.entity.AccountEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataAccountRepository;
import com.votrebanque.infrastructure.persistence.repository.SpringDataBeneficiaryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@WithMockUser(username = "admin", roles = "ADMIN")
class End2EndAddBeneficiaryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataAccountRepository accountRepository;

    @Autowired
    private SpringDataBeneficiaryRepository beneficiaryRepository;

    final String ownerNumber = "FR761234567";
    final String beneficiaryNumber = "FR769876567";

    @BeforeEach
    void cleanUp() {
        beneficiaryRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void shouldReturnCreatedAndPersistBeneficiaryWhenRequestIsValid() throws Exception {
        
        accountRepository.save(new AccountEntity(ownerNumber, "Alice", BigDecimal.valueOf(1000.0), null));
        accountRepository.save(new AccountEntity(beneficiaryNumber, "Bob", BigDecimal.valueOf(500.0), null));
        accountRepository.flush();

        String jsonRequest = """
                {
                  "label": "Bob",
                  "accountNumber": "%s",
                  "ownerName": "Bob"
                }
                """.formatted(beneficiaryNumber);

        mockMvc.perform(post("/api/accounts/" + ownerNumber + "/beneficiaries")
                //.with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label").value("Bob"))
                .andExpect(jsonPath("$.beneficiaryAccountNumber").value(beneficiaryNumber));

        var savedBeneficiary = beneficiaryRepository.findByTargetAccountNumberAndOwnerAccountNumber(beneficiaryNumber, ownerNumber).orElseThrow();
        assertThat(savedBeneficiary.getLabel()).isEqualTo("Bob");
        assertThat(savedBeneficiary.getTargetAccountNumber()).isEqualTo(beneficiaryNumber);
    }

    @Test
    void shouldReturnBadRequestWhenOwnerNameDoesNotMatchAccountOwner() throws Exception {
        accountRepository.save(new AccountEntity(ownerNumber, "Alice", BigDecimal.valueOf(1000.0), null));
        accountRepository.save(new AccountEntity(beneficiaryNumber, "Bob", BigDecimal.valueOf(500.0), null));
        accountRepository.flush();
        String jsonRequest = """
                {
                  "label": "Bob",
                  "accountNumber": "%s",
                  "ownerName": "Charlie"
                }
                """.formatted(beneficiaryNumber);

        mockMvc.perform(post("/api/accounts/" + ownerNumber + "/beneficiaries")
                //.with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        assertThat(beneficiaryRepository.findAll()).isEmpty();
    }

    @Test
    void shouldReturnBadRequestWhenAccountNumberDoesNotMatch() throws Exception {
        accountRepository.save(new AccountEntity(ownerNumber, "Alice", BigDecimal.valueOf(1000.0), null));
        accountRepository.save(new AccountEntity(beneficiaryNumber, "Bob", BigDecimal.valueOf(500.0), null));
        accountRepository.flush();

        String jsonRequest = """
                {
                  "label": "Bob",
                  "accountNumber": "FR769876578",
                  "ownerName": "Bob"
                }
                """;

        mockMvc.perform(post("/api/accounts/" + ownerNumber + "/beneficiaries")
                //.with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isNotFound());

        assertThat(beneficiaryRepository.findAll()).isEmpty();
    }
}
