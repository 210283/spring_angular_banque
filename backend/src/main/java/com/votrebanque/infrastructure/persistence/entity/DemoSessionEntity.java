package com.votrebanque.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "demo_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemoSessionEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String username;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "account_numbers", nullable = false, columnDefinition = "text[]")
    private List<String> accountNumbers;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
