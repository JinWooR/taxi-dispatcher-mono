package com.taxidispatcher.services.customer.application.dto.response;

import com.taxidispatcher.services.customer.domain.customer.Customer;

import java.time.LocalDateTime;

public class CustomerProfileResponse {

    private String customerId;
    private String accountId;
    private String name;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected CustomerProfileResponse() {
    }

    public CustomerProfileResponse(String customerId, String accountId, String name, String phone,
                               String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.customerId = customerId;
        this.accountId = accountId;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CustomerProfileResponse from(Customer customer) {
        return new CustomerProfileResponse(
                customer.getCustomerId().getValue(),
                customer.getAccountId(),
                customer.getName(),
                customer.getPhone(),
                customer.getStatus().name(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    public String getCustomerId() {
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
