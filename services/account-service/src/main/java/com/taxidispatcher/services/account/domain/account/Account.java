package com.taxidispatcher.services.account.domain.account;

import com.taxidispatcher.services.account.domain.credential.BasicCredential;
import com.taxidispatcher.services.account.domain.credential.Credential;
import com.taxidispatcher.services.account.domain.credential.OAuthCredential;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Account (Aggregate Root)
 * 계정은 여러 인증 수단(Credential)을 가질 수 있음
 */
public class Account {

    private AccountId accountId;
    private AccountStatus status;
    private List<Credential> credentials;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 새로운 Account 생성
     */
    public Account(AccountId accountId) {
        this.accountId = Objects.requireNonNull(accountId);
        this.status = AccountStatus.ACTIVE;
        this.credentials = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // JPA용 생성자
    protected Account() {
        this.credentials = new ArrayList<>();
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public List<Credential> getCredentials() {
        return new ArrayList<>(credentials);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 기본 인증 수단 추가
     */
    public void addBasicCredential(BasicCredential basicCredential) {
        Objects.requireNonNull(basicCredential);
        if (!basicCredential.getAccountId().equals(this.accountId)) {
            throw new IllegalArgumentException("Credential의 accountId가 일치하지 않습니다");
        }

        // 중복 로그인아이디 확인
        boolean exists = credentials.stream()
                .filter(c -> c instanceof BasicCredential)
                .map(c -> (BasicCredential) c)
                .anyMatch(c -> c.getLoginId().equals(basicCredential.getLoginId()));

        if (exists) {
            throw new IllegalArgumentException("이미 존재하는 로그인 ID입니다");
        }

        credentials.add(basicCredential);
        updateTimestamp();
    }

    /**
     * OAuth 인증 수단 추가
     */
    public void addOAuthCredential(OAuthCredential oauthCredential) {
        Objects.requireNonNull(oauthCredential);
        if (!oauthCredential.getAccountId().equals(this.accountId)) {
            throw new IllegalArgumentException("Credential의 accountId가 일치하지 않습니다");
        }

        credentials.add(oauthCredential);
        updateTimestamp();
    }

    /**
     * 로그인 ID로 기본 인증 수단 조회
     */
    public Optional<BasicCredential> findBasicCredential(String loginId) {
        return credentials.stream()
                .filter(c -> c instanceof BasicCredential)
                .map(c -> (BasicCredential) c)
                .filter(c -> c.getLoginId().equals(loginId))
                .findFirst();
    }

    /**
     * 계정 잠금
     */
    public void lock() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("활성 계정만 잠금할 수 있습니다");
        }
        this.status = AccountStatus.LOCKED;
        updateTimestamp();
    }

    /**
     * 계정 잠금 해제
     */
    public void unlock() {
        if (this.status != AccountStatus.LOCKED) {
            throw new IllegalStateException("잠금된 계정만 해제할 수 있습니다");
        }
        this.status = AccountStatus.ACTIVE;
        updateTimestamp();
    }

    /**
     * 계정 정지
     */
    public void suspend() {
        if (this.status == AccountStatus.DELETED) {
            throw new IllegalStateException("삭제된 계정은 정지할 수 없습니다");
        }
        this.status = AccountStatus.SUSPENDED;
        updateTimestamp();
    }

    /**
     * 계정 삭제
     */
    public void delete() {
        this.status = AccountStatus.DELETED;
        updateTimestamp();
    }

    /**
     * 활성 상태 확인
     */
    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    /**
     * 타임스탐프 업데이트
     */
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // JPA용 setter 메서드들

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Credential 직접 추가 (JPA 로딩용)
     */
    public void addCredentialDirect(Credential credential) {
        this.credentials.add(credential);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }
}
