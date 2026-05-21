package com.taxidispatcher.services.account.infrastructure.persistence;

import com.taxidispatcher.services.account.domain.account.Account;
import com.taxidispatcher.services.account.domain.account.AccountId;
import com.taxidispatcher.services.account.domain.account.AccountStatus;
import com.taxidispatcher.services.account.domain.credential.BasicCredential;
import com.taxidispatcher.services.account.domain.credential.OAuthCredential;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Account JPA 엔티티
 */
@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountJpaEntity {

    @Id
    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CredentialJpaEntity> credentials = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 도메인 모델로 변환
     */
    public Account toDomain() {
        Account account = new Account(AccountId.of(this.accountId));
        account.setStatus(this.status);
        account.setCreatedAt(this.createdAt);
        account.setUpdatedAt(this.updatedAt);

        // Credential 변환
        if (this.credentials != null) {
            this.credentials.forEach(credentialJpa -> {
                account.addCredentialDirect(credentialJpa.toDomain());
            });
        }

        return account;
    }

    /**
     * 도메인 모델에서 JPA 엔티티로 변환
     */
    public static AccountJpaEntity fromDomain(Account domain) {
        AccountJpaEntity entity = AccountJpaEntity.builder()
                .accountId(domain.getAccountId().getValue())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        domain.getCredentials().forEach(credential -> {
            CredentialJpaEntity credentialEntity;
            if (credential instanceof BasicCredential basic) {
                credentialEntity = BasicCredentialJpaEntity.fromDomain(basic);
            } else {
                credentialEntity = OAuthCredentialJpaEntity.fromDomain((OAuthCredential) credential);
            }
            credentialEntity.setAccount(entity);
            entity.credentials.add(credentialEntity);
        });

        return entity;
    }

    /**
     * 도메인에 설정된 값을 엔티티에 적용
     */
    public void updateFromDomain(Account domain) {
        this.accountId = domain.getAccountId().getValue();
        this.status = domain.getStatus();
        this.updatedAt = domain.getUpdatedAt();
    }
}
