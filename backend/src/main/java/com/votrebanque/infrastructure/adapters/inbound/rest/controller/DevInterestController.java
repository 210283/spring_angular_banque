package com.votrebanque.infrastructure.adapters.inbound.rest.controller;

import com.votrebanque.application.port.inbound.AccrueInterestUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
public class DevInterestController {

    private final AccrueInterestUseCase accrueInterestUseCase;

    public DevInterestController(AccrueInterestUseCase accrueInterestUseCase) {
        this.accrueInterestUseCase = accrueInterestUseCase;
    }

    @PostMapping("/accrue-interest")
    public ResponseEntity<String> accrueInterest() {
        int count = accrueInterestUseCase.accrueInterestForAllAccounts();
        return ResponseEntity.ok(count + " accounts processed");
    }
}