package com.taxidispatcher.services.customer.infrastructure.persistence;

import com.taxidispatcher.services.customer.domain.customer.Customer;
import com.taxidispatcher.services.customer.domain.customer.CustomerId;
import com.taxidispatcher.services.customer.domain.customer.CustomerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 프로필 JPA 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerJpaEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "VARCHAR(36)")
    private String customerId;

    @Column(name = "account_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 도메인 모델 → JPA 엔티티
     */
    public static CustomerJpaEntity from(Customer customer) {
        return CustomerJpaEntity.builder()
                .customerId(customer.getCustomerId().getValue())
                .accountId(customer.getAccountId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    /**
     * JPA 엔티티 → 도메인 모델
     */
    public Customer toModel() {
        Customer customer = new Customer(
                CustomerId.of(customerId),
                accountId,
                name,
                phone
        );
        // Customer 내부 상태 설정을 위한 reflection 대신 도메인 로직 사용
        // (여기서는 이미 저장된 상태이므로 필드 직접 접근)
        return customer;
    }
}
