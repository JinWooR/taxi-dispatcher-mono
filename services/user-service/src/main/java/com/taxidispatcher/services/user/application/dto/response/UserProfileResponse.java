package com.taxidispatcher.services.user.application.dto.response;

import com.taxidispatcher.services.user.domain.user.User;

import java.time.LocalDateTime;

public class UserProfileResponse {

    private String userId;
    private String accountId;
    private String name;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected UserProfileResponse() {
    }

    public UserProfileResponse(String userId, String accountId, String name, String phone,
                               String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.accountId = accountId;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getUserId().getValue(),
                user.getAccountId(),
                user.getName(),
                user.getPhone(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public String getUserId() {
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

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
