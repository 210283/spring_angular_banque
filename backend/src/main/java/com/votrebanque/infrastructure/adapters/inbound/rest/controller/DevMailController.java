package com.votrebanque.infrastructure.adapters.inbound.rest.controller;

import com.votrebanque.application.port.inbound.GetActivationEmailPreviewUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
public class DevMailController {

    private final GetActivationEmailPreviewUseCase getActivationEmailPreviewUseCase;

    public DevMailController(GetActivationEmailPreviewUseCase getActivationEmailPreviewUseCase) {
        this.getActivationEmailPreviewUseCase = getActivationEmailPreviewUseCase;
    }

    @GetMapping("/activation-email/{username}")
    public GetActivationEmailPreviewUseCase.EmailPreviewResult getActivationEmail(@PathVariable String username) {
        return getActivationEmailPreviewUseCase.getActivationEmail(username);
    }
}