package com.votrebanque.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.votrebanque.domain.validator.PasswordPolicyValidator;

@Configuration
public class DomainBeansConfig {

    @Bean
    public PasswordPolicyValidator passwordPolicyValidator() {
        return new PasswordPolicyValidator();
    }
}
