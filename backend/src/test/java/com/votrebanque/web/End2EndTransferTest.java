package com.votrebanque.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.votrebanque.TestcontainersConfiguration;
import com.votrebanque.domain.model.AccountType;
import com.votrebanque.infrastructure.persistence.entity.AccountEntity;
import com.votrebanque.infrastructure.persistence.entity.BeneficiaryEntity;
import com.votrebanque.infrastructure.persistence.repository.SpringDataAccountRepository;
import com.votrebanque.infrastructure.persistence.repository.SpringDataBeneficiaryRepository;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional 
@WithMockUser(username = "admin", roles = "ADMIN")
class End2EndTransferTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SpringDataAccountRepository accountRepository;

  @Autowired
  private SpringDataBeneficiaryRepository beneficiaryRepository; 

  @BeforeEach
  void setUp() {
    beneficiaryRepository.deleteAll();
    accountRepository.deleteAll();
  }

  private void createBeneficiaryRelation(String label, String beneficiaryAccountNumber, AccountEntity sourceAccount) {
    BeneficiaryEntity beneficiary = new BeneficiaryEntity();
    beneficiary.setId(UUID.randomUUID().toString()); 
    beneficiary.setLabel(label);
    beneficiary.setTargetAccountNumber(beneficiaryAccountNumber);
    beneficiary.setOwnerAccountNumber(sourceAccount.getAccountNumber());
    beneficiaryRepository.save(beneficiary);
  }

  @Test
  void shouldReturnOkWhenTransferIsSuccessful() throws Exception {
    AccountEntity alice = accountRepository.save(new AccountEntity("FR761234567", "Alice", BigDecimal.valueOf(1000.0), AccountType.CURRENT, BigDecimal.ZERO, LocalDate.now(), null));
    accountRepository.save(new AccountEntity("FR769876567", "Bob", BigDecimal.valueOf(500.0), AccountType.CURRENT, BigDecimal.ZERO, LocalDate.now(), null));

    createBeneficiaryRelation("Bob the beneficiary", "FR769876567", alice);

    String jsonRequest = """
            {
              "sourceAccountNumber": "FR761234567",
              "destinationAccountNumber": "FR769876567",
              "amount": 200.00
            }
            """;

    mockMvc.perform(post("/api/accounts/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(content().string("Transfer successfully completed !"));
  }

  @Test
  void shouldReturnBadRequestWhenTransferFailsDueToInsufficientFunds() throws Exception {
    AccountEntity alice = accountRepository.save(new AccountEntity("FR761234567", "Alice", BigDecimal.valueOf(50.0), AccountType.CURRENT, BigDecimal.ZERO, LocalDate.now(), null));
    accountRepository.save(new AccountEntity("FR769876567", "Bob", BigDecimal.valueOf(500.0), AccountType.CURRENT, BigDecimal.ZERO, LocalDate.now(), null));

    createBeneficiaryRelation("Bob the beneficiary", "FR769876567", alice);
    
    String jsonRequest = """
            {
              "sourceAccountNumber": "FR761234567",
              "destinationAccountNumber": "FR769876567",
              "amount": 200.00
            }
            """;

    mockMvc.perform(post("/api/accounts/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
            .andExpect(status().isBadRequest());
            
    AccountEntity sourcePostTransfer = accountRepository.findById("FR761234567").orElseThrow();
    AccountEntity destPostTransfer = accountRepository.findById("FR769876567").orElseThrow();
    
    org.assertj.core.api.Assertions.assertThat(sourcePostTransfer.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(50.0));
    org.assertj.core.api.Assertions.assertThat(destPostTransfer.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(500.0));
  }

  @Test
  void shouldReturnBadRequestWhenTransferFailsDueToNotBeneficiary() throws Exception {
    accountRepository.save(new AccountEntity("FR761234567", "Alice", BigDecimal.valueOf(1000.0), AccountType.CURRENT, BigDecimal.ZERO, LocalDate.now(), null));
    accountRepository.save(new AccountEntity("FR769876567", "Bob", BigDecimal.valueOf(500.0), AccountType.CURRENT, BigDecimal.ZERO, LocalDate.now(), null));

    String jsonRequest = """
            {
              "sourceAccountNumber": "FR761234567",
              "destinationAccountNumber": "FR769876567",
              "amount": 200.00
            }
            """;

    mockMvc.perform(post("/api/accounts/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
            .andExpect(status().isBadRequest());

    AccountEntity sourcePostTransfer = accountRepository.findById("FR761234567").orElseThrow();
    AccountEntity destPostTransfer = accountRepository.findById("FR769876567").orElseThrow();

    org.assertj.core.api.Assertions.assertThat(sourcePostTransfer.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000.0));
    org.assertj.core.api.Assertions.assertThat(destPostTransfer.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(500.0));
  }
}