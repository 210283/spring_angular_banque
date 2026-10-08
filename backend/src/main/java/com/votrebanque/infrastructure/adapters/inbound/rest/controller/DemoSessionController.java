package com.votrebanque.infrastructure.adapters.inbound.rest.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.votrebanque.application.port.inbound.DemoSessionResult;
import com.votrebanque.application.port.inbound.OpenDemoSessionUseCase;
import com.votrebanque.infrastructure.adapters.inbound.rest.response.DemoSessionResponse;
import com.votrebanque.infrastructure.adapters.inbound.rest.security.DemoSessionRateLimiter;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/demo")
@ConditionalOnProperty(name = "app.demo.session.enabled", havingValue = "true")
public class DemoSessionController {

    private final OpenDemoSessionUseCase openDemoSessionUseCase;
    private final DemoSessionRateLimiter rateLimiter;

    public DemoSessionController(OpenDemoSessionUseCase openDemoSessionUseCase, DemoSessionRateLimiter rateLimiter) {
        this.openDemoSessionUseCase = openDemoSessionUseCase;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/session")
    public ResponseEntity<DemoSessionResponse> openSession(HttpServletRequest request) {
        String clientIp = resolveClientIp(request);

        if (!rateLimiter.tryAcquire(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        DemoSessionResult result = openDemoSessionUseCase.openDemoSession();

        return ResponseEntity.status(HttpStatus.CREATED).body(new DemoSessionResponse(
            result.clientToken(), result.adminToken(), result.mainAccountNumber(), result.secondaryAccountNumber(), result.expiresAt()
        ));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
