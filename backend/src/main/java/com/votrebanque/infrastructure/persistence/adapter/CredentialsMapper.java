package com.votrebanque.infrastructure.persistence.adapter;

import com.votrebanque.domain.model.AccountId;
import com.votrebanque.domain.model.Credentials;
import com.votrebanque.infrastructure.persistence.entity.CredentialsEntity;

class CredentialsMapper {

    static Credentials toDomain(CredentialsEntity entity) {
        return Credentials.reconstruct(
            entity.getUsername(),
            new AccountId(entity.getAccountNumber()),
            entity.getHashedPassword(),
            entity.isMustChangePassword(),
            entity.getFailedLoginAttempts(),
            entity.getLockedUntil()
        );
    }

    static CredentialsEntity toEntity(Credentials credentials) {
        return new CredentialsEntity(
            credentials.getUsername(),
            credentials.getAccountId().value(),
            credentials.getHashedPassword(),
            credentials.mustChangePassword(),
            credentials.getFailedLoginAttempts(),
            credentials.getLockedUntil(),
            null, // version
            null, // createdAt
            null  // updatedAt
        );
    }

    static CredentialsEntity updateEntity(CredentialsEntity existing, Credentials credentials) {
        existing.setHashedPassword(credentials.getHashedPassword());
        existing.setMustChangePassword(credentials.mustChangePassword());
        existing.setFailedLoginAttempts(credentials.getFailedLoginAttempts());
        existing.setLockedUntil(credentials.getLockedUntil());
        return existing;
    }
}
