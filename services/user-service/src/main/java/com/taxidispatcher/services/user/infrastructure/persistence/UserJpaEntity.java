package com.taxidispatcher.services.user.infrastructure.persistence;

import com.taxidispatcher.services.user.domain.user.User;
import com.taxidispatcher.services.user.domain.user.UserId;
import com.taxidispatcher.services.user.domain.user.UserStatus;
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
public class UserJpaEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "VARCHAR(36)")
    private String userId;

    @Column(name = "account_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String accountId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 도메인 모델 → JPA 엔티티
     */
    public static UserJpaEntity from(User user) {
        return UserJpaEntity.builder()
                .userId(user.getUserId().getValue())
                .accountId(user.getAccountId())
                .name(user.getName())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * JPA 엔티티 → 도메인 모델
     */
    public User toModel() {
        User user = new User(
                UserId.of(userId),
                accountId,
                name,
                phone
        );
        // User 내부 상태 설정을 위한 reflection 대신 도메인 로직 사용
        // (여기서는 이미 저장된 상태이므로 필드 직접 접근)
        return user;
    }
}
