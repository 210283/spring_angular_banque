package com.votrebanque.infrastructure.adapters.inbound.rest.controller;

import com.votrebanque.application.port.inbound.ActivateAccountUseCase;
import com.votrebanque.application.port.inbound.LoginUseCase;
import com.votrebanque.infrastructure.adapters.inbound.rest.request.ActivateAccountRequest;
import com.votrebanque.infrastructure.adapters.inbound.rest.request.LoginRequest;
import com.votrebanque.infrastructure.adapters.inbound.rest.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;

    public AuthController(LoginUseCase loginUseCase, ActivateAccountUseCase activateAccountUseCase) {
        this.loginUseCase = loginUseCase;
        this.activateAccountUseCase = activateAccountUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = loginUseCase.login(request.username(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activate(@RequestBody ActivateAccountRequest request) {
        activateAccountUseCase.activateAccount(
            request.username(), request.token(), request.newPassword()
        );
        return ResponseEntity.ok().build();
    }
}
