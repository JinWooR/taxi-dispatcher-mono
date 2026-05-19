package com.taxidispatcher.services.user.domain.user;

import java.time.LocalDateTime;

/**
 * 승객 프로필 Aggregate Root
 * 사용자의 이름, 전화번호 등 프로필 정보 관리
 */
public class User {

    private UserId userId;
    private String accountId;         // Account 참조 (타 서비스)
    private String name;              // 실명
    private String phone;             // 전화번호
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected User() {
    }

    public User(UserId userId, String accountId, String name, String phone) {
        this.userId = userId;
        this.accountId = accountId;
        this.name = name;
        this.phone = phone;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 프로필 수정
     */
    public void update(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 탈퇴 (soft delete)
     */
    public void delete() {
        this.status = UserStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public UserId getUserId() {
        return userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
