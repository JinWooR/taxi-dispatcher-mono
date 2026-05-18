package com.taxidispatcher.services.account.domain.account;

/**
 * 계정 상태
 */
public enum AccountStatus {
    ACTIVE("활성"),
    LOCKED("잠금"),
    SUSPENDED("정지"),
    DELETED("삭제");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
