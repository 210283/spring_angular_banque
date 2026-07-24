package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.domain.model.ActivationToken;
import com.votrebanque.infrastructure.persistence.entity.ActivationTokenEntity;

class ActivationTokenMapper {

    static ActivationToken toDomain(ActivationTokenEntity entity) {
        return ActivationToken.reconstruct(
            entity.getId(),
            entity.getHashedToken(),
            entity.getUsername(),
            entity.getExpiresAt(),
            entity.isUsed(),
            entity.isEmailSent()
        );
    }

    static ActivationTokenEntity toEntity(ActivationToken token) {
        return new ActivationTokenEntity(
            token.id(),
            token.getUsername(),
            token.getHashedToken(),
            token.getExpiresAt(),
            token.isUsed(),
            token.isEmailSent(),
            null 
        );
    }

    static ActivationTokenEntity updateEntity(ActivationTokenEntity existing, ActivationToken token) {
        existing.setUsed(token.isUsed());
        existing.setEmailSent(token.isEmailSent());
        // username, hashedToken, expiresAt are not mutable
        return existing;
    }
}
