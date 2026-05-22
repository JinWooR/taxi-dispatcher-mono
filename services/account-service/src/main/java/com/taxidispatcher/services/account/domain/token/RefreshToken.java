package com.taxidispatcher.services.account.domain.token;

import com.taxidispatcher.services.account.domain.account.AccountId;

import java.time.LocalDateTime;
import java.util.Objects;

public class RefreshToken {

    private final TokenId tokenId;
    private final AccountId accountId;
    private final String tokenHash;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;

    public RefreshToken(TokenId tokenId, AccountId accountId, String tokenHash, LocalDateTime expiresAt) {
        this.tokenId = Objects.requireNonNull(tokenId);
        this.accountId = Objects.requireNonNull(accountId);
        this.tokenHash = Objects.requireNonNull(tokenHash);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.createdAt = LocalDateTime.now();
    }

    protected RefreshToken() {
        this.tokenId = null;
        this.accountId = null;
        this.tokenHash = null;
        this.expiresAt = null;
        this.createdAt = null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public TokenId getTokenId() { return tokenId; }
    public AccountId getAccountId() { return accountId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
