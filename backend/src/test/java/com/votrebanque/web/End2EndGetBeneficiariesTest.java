package com.votrebanque.web;

import com.votrebanque.infrastructure.persistence.repository.SpringDataAccountRepository;
import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.infrastructure.persistence.entity.AccountEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataBeneficiaryRepository;
import com.votrebanque.infrastructure.persistence.entity.BeneficiaryEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
//@Transactional
public class End2EndGetBeneficiariesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataAccountRepository accountRepository;

    @Autowired
    private SpringDataBeneficiaryRepository beneficiaryRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private static final String ACCOUNT_NUMBER = "FR761234567";
    private static final String BENEFICIARY_1_ACCOUNT = "FR769876567";
    private static final String BENEFICIARY_2_ACCOUNT = "FR761112223";

    @BeforeEach
    void setup() {
        accountRepository.deleteAll();
        beneficiaryRepository.deleteAll();

        accountRepository.saveAndFlush(new AccountEntity(ACCOUNT_NUMBER, "Charlie", BigDecimal.valueOf(1234.50), null));
        accountRepository.saveAndFlush(new AccountEntity(BENEFICIARY_1_ACCOUNT, "Alice", BigDecimal.valueOf(200.00), null));
        accountRepository.saveAndFlush(new AccountEntity(BENEFICIARY_2_ACCOUNT, "Bob", BigDecimal.valueOf(300.50), null));

        BeneficiaryEntity b1 = new BeneficiaryEntity(UUID.randomUUID().toString(), "b-id-1", "FR769876567", ACCOUNT_NUMBER, null);
        BeneficiaryEntity b2 = new BeneficiaryEntity(UUID.randomUUID().toString(), "b-id-2", "FR761112223", ACCOUNT_NUMBER, null);

        beneficiaryRepository.saveAndFlush(b1);
        beneficiaryRepository.saveAndFlush(b2);
    }

    @Test
    void shouldReturnBeneficiariesListForExistingAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/" + ACCOUNT_NUMBER + "/beneficiaries")
                .with(user("admin").roles("ADMIN"))
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
        accountRepository.saveAndFlush(emptyAccount);

        mockMvc.perform(get("/api/accounts/" + emptyAccountStr + "/beneficiaries")
                .with(user("admin").roles("ADMIN"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}