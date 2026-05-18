package com.taxidispatcher.services.account.infrastructure.persistence;

import com.taxidispatcher.services.account.domain.account.AccountId;
import com.taxidispatcher.services.account.domain.credential.Credential;
import com.taxidispatcher.services.account.domain.credential.CredentialId;
import com.taxidispatcher.services.account.domain.credential.OAuthCredential;
import com.taxidispatcher.services.account.domain.credential.OAuthKind;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * OAuthCredential JPA 엔티티
 */
@Entity
@DiscriminatorValue("OAUTH")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OAuthCredentialJpaEntity extends CredentialJpaEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_kind", nullable = false, length = 20)
    private OAuthKind oAuthKind;

    @Column(name = "iss", nullable = false, length = 255)
    private String iss;

    @Column(name = "sub", nullable = false, length = 255)
    private String sub;

    @Column(name = "email_link", length = 255)
    private String emailLink;

    public OAuthCredentialJpaEntity(String credentialId, String accountId, OAuthKind oAuthKind, String iss, String sub, String emailLink, LocalDateTime registeredAt) {
        super(credentialId, accountId, registeredAt);
        this.oAuthKind = oAuthKind;
        this.iss = iss;
        this.sub = sub;
        this.emailLink = emailLink;
    }

    @Override
    public Credential toDomain() {
        return new OAuthCredential(
                CredentialId.of(this.getCredentialId()),
                AccountId.of(this.getAccountId()),
                this.oAuthKind,
                this.iss,
                this.sub,
                this.emailLink
        );
    }

    /**
     * 도메인에서 JPA 엔티티로 변환
     */
    public static OAuthCredentialJpaEntity fromDomain(OAuthCredential domain) {
        return OAuthCredentialJpaEntity.builder()
                .credentialId(domain.getCredentialId().getValue())
                .accountId(domain.getAccountId().getValue())
                .oAuthKind(domain.getOAuthKind())
                .iss(domain.getIss())
                .sub(domain.getSub())
                .emailLink(domain.getEmailLink())
                .registeredAt(domain.getRegisteredAt())
                .lastUsedAt(domain.getLastUsedAt())
                .build();
    }
}
