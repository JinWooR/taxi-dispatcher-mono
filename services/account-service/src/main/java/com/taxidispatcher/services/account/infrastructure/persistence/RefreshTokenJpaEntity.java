package com.taxidispatcher.services.account.infrastructure.persistence;

import com.taxidispatcher.services.account.domain.account.AccountId;
import com.taxidispatcher.services.account.domain.token.RefreshToken;
import com.taxidispatcher.services.account.domain.token.TokenId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenJpaEntity {

    @Id
    @Column(name = "token_id", nullable = false, length = 36)
    private String tokenId;

    @Column(name = "account_id", nullable = false, length = 36)
    private String accountId;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RefreshToken toDomain() {
        return new RefreshToken(
                TokenId.of(this.tokenId),
                AccountId.of(this.accountId),
                this.tokenHash,
                this.expiresAt
        );
    }

    public static RefreshTokenJpaEntity fromDomain(RefreshToken domain) {
        return RefreshTokenJpaEntity.builder()
                .tokenId(domain.getTokenId().getValue())
                .accountId(domain.getAccountId().getValue())
                .tokenHash(domain.getTokenHash())
                .expiresAt(domain.getExpiresAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
