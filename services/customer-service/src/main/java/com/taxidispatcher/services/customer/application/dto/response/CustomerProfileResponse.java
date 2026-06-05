package com.taxidispatcher.services.customer.application.dto.response;

import com.taxidispatcher.services.customer.domain.customer.Customer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class CustomerProfileResponse {

    @Schema(description = "고객 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String customerId;

    @Schema(description = "연결된 계정 ID (UUID)", example = "660e8400-e29b-41d4-a716-446655440001")
    private String accountId;

    @Schema(description = "고객 이름", example = "홍길동")
    private String name;

    @Schema(description = "고객 연락처", example = "010-1234-5678")
    private String phone;

    @Schema(description = "고객 상태", example = "ACTIVE")
    private String status;

    @Schema(description = "생성 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
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
