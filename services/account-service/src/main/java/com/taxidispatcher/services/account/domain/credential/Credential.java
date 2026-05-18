package com.taxidispatcher.services.account.domain.credential;

import com.taxidispatcher.services.account.domain.account.AccountId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 인증 수단 추상 클래스 (Entity)
 */
public abstract class Credential {

    protected CredentialId credentialId;
    protected AccountId accountId;
    protected LocalDateTime registeredAt;
    protected LocalDateTime lastUsedAt;

    protected Credential(CredentialId credentialId, AccountId accountId) {
        this.credentialId = Objects.requireNonNull(credentialId);
        this.accountId = Objects.requireNonNull(accountId);
        this.registeredAt = LocalDateTime.now();
        this.lastUsedAt = null;
    }

    // 생성자 (JPA용)
    protected Credential() {
    }

    public CredentialId getCredentialId() {
        return credentialId;
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    /**
     * 마지막 사용 시간 업데이트
     */
    public void updateLastUsedAt() {
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * 인증 수단의 타입
     */
    public abstract String getType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credential credential = (Credential) o;
        return Objects.equals(credentialId, credential.credentialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(credentialId);
    }
}
