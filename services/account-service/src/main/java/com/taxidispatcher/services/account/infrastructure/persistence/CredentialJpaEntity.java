package com.taxidispatcher.services.account.infrastructure.persistence;

import com.taxidispatcher.services.account.domain.credential.Credential;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Credential JPA 엔티티 (상속 구조)
 * 단일 테이블 상속 전략 사용
 */
@Entity
@Table(name = "credentials", indexes = {
        @Index(name = "idx_credential_id", columnList = "credential_id", unique = true),
        @Index(name = "idx_account_id", columnList = "account_id"),
        @Index(name = "idx_credential_type", columnList = "credential_type")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "credential_type", discriminatorType = DiscriminatorType.STRING, length = 20)
@Getter
@NoArgsConstructor
@SuperBuilder
public abstract class CredentialJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credential_id", unique = true, nullable = false, length = 36)
    private String credentialId;

    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false, nullable = false)
    private AccountJpaEntity account;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    protected CredentialJpaEntity(String credentialId, String accountId, LocalDateTime registeredAt) {
        this.credentialId = credentialId;
        this.accountId = accountId;
        this.registeredAt = registeredAt;
    }

    /**
     * 도메인 모델로 변환
     */
    public abstract Credential toDomain();

    /**
     * 마지막 사용 시간 업데이트
     */
    public void updateLastUsedAt() {
        this.lastUsedAt = LocalDateTime.now();
    }

    public void setAccount(AccountJpaEntity account) {
        this.account = account;
    }
}
