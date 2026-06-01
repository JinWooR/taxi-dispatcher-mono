package com.taxidispatcher.services.customer.domain.customer;

import java.time.LocalDateTime;

/**
 * 승객 프로필 Aggregate Root
 * 사용자의 이름, 전화번호 등 프로필 정보 관리
 */
public class Customer {

    private CustomerId customerId;
    private String accountId;         // Account 참조 (타 서비스)
    private String name;              // 실명
    private String phone;             // 전화번호
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Customer() {
    }

    public Customer(CustomerId customerId, String accountId, String name, String phone) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.name = name;
        this.phone = phone;
        this.status = CustomerStatus.ACTIVE;
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
        this.status = CustomerStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public CustomerId getCustomerId() {
        return customerId;
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

    public CustomerStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }
}
