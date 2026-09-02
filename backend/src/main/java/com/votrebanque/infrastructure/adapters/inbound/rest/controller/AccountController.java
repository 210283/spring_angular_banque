package com.votrebanque.infrastructure.adapters.inbound.rest.controller;

import com.votrebanque.application.port.inbound.AccountOpeningResult;
import com.votrebanque.application.port.inbound.AddBeneficiaryUseCase;
import com.votrebanque.application.port.inbound.GetAccountSummaryUseCase;
import com.votrebanque.application.port.inbound.GetBeneficiariesUseCase;
import com.votrebanque.application.port.inbound.GetLinkedSavingsAccountsUseCase;
import com.votrebanque.application.port.inbound.GetMyAccountUseCase;
import com.votrebanque.application.port.inbound.OpenAccountUseCase;
import com.votrebanque.application.port.inbound.TransferUseCase;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Money;
import com.votrebanque.infrastructure.adapters.inbound.rest.request.AddBeneficiaryRequest;
import com.votrebanque.infrastructure.adapters.inbound.rest.request.OpenAccountRequest;
import com.votrebanque.infrastructure.adapters.inbound.rest.request.TransferRequest;
import com.votrebanque.infrastructure.adapters.inbound.rest.response.AccountCreationResponse;
import com.votrebanque.infrastructure.adapters.inbound.rest.response.AccountSummaryResponse;
import com.votrebanque.infrastructure.adapters.inbound.rest.response.BeneficiaryResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final OpenAccountUseCase openAccountUseCase;
    private final TransferUseCase transferUseCase;
    private final GetAccountSummaryUseCase getAccountSummaryUseCase;
    private final AddBeneficiaryUseCase addBeneficiaryUseCase;
    private final GetBeneficiariesUseCase getBeneficiariesUseCase;
    private final GetMyAccountUseCase getMyAccountUseCase;
    private final GetLinkedSavingsAccountsUseCase getLinkedSavingsAccountsUseCase;
    
    @PostMapping("/transfer")
    public ResponseEntity<String> makeStranfer(@RequestBody TransferRequest request) {        
        AccountId sourceId = new AccountId(request.sourceAccountNumber());
        Money amountToSend = new Money(request.amount());

        transferUseCase.makeTransfer(sourceId, request.destinationAccountNumber(), amountToSend);

        return ResponseEntity.ok("Transfer successfully completed !");
    }

    @PostMapping
    public ResponseEntity<AccountCreationResponse> openAccount(@RequestBody OpenAccountRequest request) {   
        AccountOpeningResult result = openAccountUseCase.openAccount(
            request.owner(),
            new Money(request.initialDeposit()),
            request.accountType(),
            request.linkedAccountNumber()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(AccountCreationResponse.from(result));
    }

    @GetMapping("/{accountNumber}/summary")
    public ResponseEntity<AccountSummaryResponse> getAccountSummary(@PathVariable String accountNumber) {
        AccountId accountId = new AccountId(accountNumber);
        var summary = getAccountSummaryUseCase.getAccountSummary(accountId);

        AccountSummaryResponse response = new AccountSummaryResponse(
            summary.accountId().value(),
            summary.owner(),
            summary.balance().amount(),
            summary.accountType(),
            summary.interestRate()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountNumber}/beneficiaries")
    public ResponseEntity<BeneficiaryResponse> addBeneficiary(@PathVariable String accountNumber,
                                                            @RequestBody AddBeneficiaryRequest request) {
        var beneficiary = addBeneficiaryUseCase.addBeneficiary(
                new AccountId(accountNumber),
                request.label(),
                new AccountId(request.accountNumber()),
                request.ownerName()
        );

        var targetAccountSummary = getAccountSummaryUseCase.getAccountSummary(new AccountId(request.accountNumber()));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new BeneficiaryResponse(
                    beneficiary.id(),
                    beneficiary.label(),
                    beneficiary.accountId().value(),
                    request.ownerName(),
                    targetAccountSummary.accountType()
                )
        );
    }

    @GetMapping("/{accountNumber}/beneficiaries")
    public ResponseEntity<List<BeneficiaryResponse>> getBeneficiaries(@PathVariable String accountNumber) {
        var beneficiaries = getBeneficiariesUseCase.getBeneficiaries(new AccountId(accountNumber));

        List<BeneficiaryResponse> response = beneficiaries.stream()
                .map(b -> new BeneficiaryResponse(b.id(), b.label(), b.accountId().value(), b.accountOwnerName(), b.accountType()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AccountSummaryResponse> getMyAccount(java.security.Principal principal) {
        var summary = getMyAccountUseCase.getMyAccount(principal.getName());

        AccountSummaryResponse response = new AccountSummaryResponse(
            summary.accountId().value(),
            summary.owner(),
            summary.balance().amount(),
            summary.accountType(),
            summary.interestRate()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/savings-accounts")
    public ResponseEntity<List<AccountSummaryResponse>> getMySavingsAccounts(java.security.Principal principal) {
        var accounts = getLinkedSavingsAccountsUseCase.getLinkedSavingsAccounts(principal.getName());

        List<AccountSummaryResponse> response = accounts.stream()
            .map(s -> new AccountSummaryResponse(s.accountId().value(), s.owner(), s.balance().amount(), s.accountType(), s.interestRate()))
            .toList();

        return ResponseEntity.ok(response);
    }
}
