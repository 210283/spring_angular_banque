package com.votrebanque.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "activation_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivationTokenEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 60)
    private String hashedToken;

    @Column(nullable = false)
    private Instant expiresAt;

    private boolean used;

    @Column(nullable = false)
    private boolean emailSent;

    @Version
    private Long version;
}   
