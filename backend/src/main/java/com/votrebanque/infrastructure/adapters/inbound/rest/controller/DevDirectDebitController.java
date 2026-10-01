package com.votrebanque.infrastructure.adapters.inbound.rest.controller;

import com.votrebanque.application.port.inbound.ExecuteDirectDebitsUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@ConditionalOnProperty(name = "app.demo.endpoints.enabled", havingValue = "true")
public class DevDirectDebitController {

    private final ExecuteDirectDebitsUseCase executeDirectDebitsUseCase;

    public DevDirectDebitController(ExecuteDirectDebitsUseCase executeDirectDebitsUseCase) {
        this.executeDirectDebitsUseCase = executeDirectDebitsUseCase;
    }

    @PostMapping("/execute-direct-debits")
    public ResponseEntity<String> executeDirectDebits() {
        int count = executeDirectDebitsUseCase.executeDueDirectDebits();
        return ResponseEntity.ok(count + " direct debit(s) executed");
    }
}
