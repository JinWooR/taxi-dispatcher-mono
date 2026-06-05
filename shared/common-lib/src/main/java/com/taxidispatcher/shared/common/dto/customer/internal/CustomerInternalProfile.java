package com.taxidispatcher.shared.common.dto.customer.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 고객 프로필 내부 통신 DTO
 * customer-service의 내부 API 응답에 사용
 * 서비스 간 통신용으로 공유
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerInternalProfile {

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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime updatedAt;
}
