package com.votrebanque.web;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.SpringBootTest;


import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.application.port.inbound.RegisterUserUseCase;
import com.votrebanque.application.port.outbound.CredentialsRepositoryPort;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.infrastructure.persistence.entity.AccountEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataAccountRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
//@Transactional 
@WithMockUser(username = "admin", roles = "ADMIN")
public class End2EndOpenAccountTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataAccountRepository jpaRepository;

    @MockitoSpyBean
    private CredentialsRepositoryPort credentialsRepository;

    @MockitoSpyBean
    private RegisterUserUseCase registerUserUseCase;

    @AfterEach
    void cleanDatabase() {
        jpaRepository.deleteAll();
        org.mockito.Mockito.reset(registerUserUseCase);
    }
    
    @Test
    void shouldReturnCreatedStatusAndAccountIdWhenOpeningAccount() throws Exception {
        // Given
        String jsonRequest = """
                {
                "owner": "Charlie",
                "initialDeposit": 150.00
                }
                """;

        // When & Then
        String jsonResponse = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated()) // Code HTTP 201
                .andReturn()
                .getResponse()
                .getContentAsString();

        String generatedId = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.accountId");
        String generatedUsername = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.username");

        // Then : bank account have been created
        AccountEntity savedEntity = jpaRepository.findById(generatedId).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(savedEntity.getOwner()).isEqualTo("Charlie");
        org.assertj.core.api.Assertions.assertThat(savedEntity.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150.00));

        // Then : credentials put in base
        org.assertj.core.api.Assertions.assertThat(generatedUsername).matches("^\\d{11}$");

        Credentials savedCredentials = credentialsRepository.findByUsername(generatedUsername).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(savedCredentials.mustChangePassword()).isTrue();
        org.assertj.core.api.Assertions.assertThat(savedCredentials.isLocked()).isFalse();
    }

    @Test
    void shouldReturnBadRequestWhenOpeningAccountWithDepositLessThanTwenty() throws Exception {
        // Given
        String jsonRequest = """
                {
                  "owner": "Charlie",
                  "initialDeposit": 10.00
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest()); // 400 Bad Request
    }

    @Test
    void shouldNotCreateOrphanAccountWhenUserRegistrationFails() throws Exception {
        long accountCountBefore = jpaRepository.count();

        // Given : forcing the creation of login credentials to fail
        org.mockito.Mockito.doThrow(new IllegalStateException("Simulated registration failure"))
            .when(registerUserUseCase)
            .registerUser(org.mockito.ArgumentMatchers.any());

        String jsonRequest = """
                {
                "owner": "Charlie",
                "initialDeposit": 150.00
                }
                """;

        // When : the request fails because OpenAccountService.openAccount() propagates the exception
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.detail").value("Internal server error"));

        // Then : thanks to @Transactional on openAccount(), the bank account was not persisted in the database
        long accountCount = jpaRepository.count();
        org.assertj.core.api.Assertions.assertThat(accountCount).isEqualTo(accountCountBefore);
    }
}
