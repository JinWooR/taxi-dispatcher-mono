package com.taxidispatcher.services.account.infrastructure.persistence;

import com.taxidispatcher.services.account.domain.account.AccountId;
import com.taxidispatcher.services.account.domain.credential.BasicCredential;
import com.taxidispatcher.services.account.domain.credential.Credential;
import com.taxidispatcher.services.account.domain.credential.CredentialId;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * BasicCredential JPA 엔티티
 */
@Entity
@DiscriminatorValue("BASIC")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BasicCredentialJpaEntity extends CredentialJpaEntity {

    @Column(name = "login_id", unique = true, nullable = false, length = 255)
    private String loginId;

    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashedPassword;

    public BasicCredentialJpaEntity(String credentialId, String accountId, String loginId, String hashedPassword, LocalDateTime registeredAt) {
        super(credentialId, accountId, registeredAt);
        this.loginId = loginId;
        this.hashedPassword = hashedPassword;
    }

    @Override
    public Credential toDomain() {
        return new BasicCredential(
                CredentialId.of(this.getCredentialId()),
                AccountId.of(this.getAccountId()),
                this.loginId,
                this.hashedPassword
        );
    }

    /**
     * 도메인에서 JPA 엔티티로 변환
     */
    public static BasicCredentialJpaEntity fromDomain(BasicCredential domain) {
        return BasicCredentialJpaEntity.builder()
                .credentialId(domain.getCredentialId().getValue())
                .accountId(domain.getAccountId().getValue())
                .loginId(domain.getLoginId())
                .hashedPassword(domain.getHashedPassword())
                .registeredAt(domain.getRegisteredAt())
                .lastUsedAt(domain.getLastUsedAt())
                .build();
    }
}
