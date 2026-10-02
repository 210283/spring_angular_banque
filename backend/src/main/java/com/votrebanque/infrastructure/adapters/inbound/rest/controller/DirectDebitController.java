package com.votrebanque.infrastructure.adapters.inbound.rest.controller;

import com.votrebanque.application.port.inbound.CancelDirectDebitUseCase;
import com.votrebanque.application.port.inbound.CreateDirectDebitUseCase;
import com.votrebanque.application.port.inbound.GetDirectDebitsUseCase;
import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Money;
import com.votrebanque.infrastructure.adapters.inbound.rest.request.CreateDirectDebitRequest;
import com.votrebanque.infrastructure.adapters.inbound.rest.response.DirectDebitResponse;
import com.votrebanque.infrastructure.security.AccountAccessGuard;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts/{accountNumber}/direct-debits")
public class DirectDebitController {

    private final CreateDirectDebitUseCase createDirectDebitUseCase;
    private final CancelDirectDebitUseCase cancelDirectDebitUseCase;
    private final GetDirectDebitsUseCase getDirectDebitsUseCase;
    private final AccountAccessGuard accountAccessGuard;

    public DirectDebitController(CreateDirectDebitUseCase createDirectDebitUseCase,
                                  CancelDirectDebitUseCase cancelDirectDebitUseCase,
                                  GetDirectDebitsUseCase getDirectDebitsUseCase,
                                  AccountAccessGuard accountAccessGuard) {
        this.createDirectDebitUseCase = createDirectDebitUseCase;
        this.cancelDirectDebitUseCase = cancelDirectDebitUseCase;
        this.getDirectDebitsUseCase = getDirectDebitsUseCase;
        this.accountAccessGuard = accountAccessGuard;
    }

    @PostMapping
    public ResponseEntity<DirectDebitResponse> create(@PathVariable String accountNumber,
                                                        @RequestBody CreateDirectDebitRequest request,
                                                        Authentication authentication) {
        accountAccessGuard.requireAccess(authentication, accountNumber);
        var result = createDirectDebitUseCase.createDirectDebit(
            new AccountId(accountNumber),
            new AccountId(request.beneficiaryAccountNumber()),
            new Money(request.amount()),
            request.frequency(),
            request.startDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new DirectDebitResponse(result.id(), request.beneficiaryAccountNumber(), null,
                request.amount(), request.frequency().name(), result.nextExecutionDate(), true)
        );
    }

    @GetMapping
    public ResponseEntity<List<DirectDebitResponse>> list(@PathVariable String accountNumber,
                                                           Authentication authentication) {
        accountAccessGuard.requireAccess(authentication, accountNumber);
        var directDebits = getDirectDebitsUseCase.getDirectDebits(new AccountId(accountNumber));

        List<DirectDebitResponse> response = directDebits.stream()
            .map(d -> new DirectDebitResponse(d.id(), d.beneficiaryAccountId().value(), d.beneficiaryLabel(),
                d.amount().amount(), d.frequency().name(), d.nextExecutionDate(), d.active()))
            .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{directDebitId}")
    public ResponseEntity<Void> cancel(@PathVariable String accountNumber, @PathVariable String directDebitId,
                                       Authentication authentication) {
        accountAccessGuard.requireAccess(authentication, accountNumber);
        cancelDirectDebitUseCase.cancelDirectDebit(new AccountId(accountNumber), directDebitId);
        return ResponseEntity.noContent().build();
    }
}
