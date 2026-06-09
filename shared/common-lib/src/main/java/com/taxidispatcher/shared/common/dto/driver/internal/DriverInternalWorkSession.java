package com.taxidispatcher.shared.common.dto.driver.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 기사 근무 세션 내부 통신 DTO
 * driver-service의 내부 API 응답에 사용
 * 서비스 간 통신용으로 공유
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DriverInternalWorkSession(

        @Schema(description = "근무 세션 ID (UUID)", example = "990e8400-e29b-41d4-a716-446655440004")
        String workSessionId,

        @Schema(description = "기사 ID (UUID)", example = "770e8400-e29b-41d4-a716-446655440002")
        String driverId,

        @Schema(description = "근무 세션 상태 (IN_PROGRESS / ENDED)", example = "IN_PROGRESS")
        String status,

        @Schema(description = "근무 시작 시각 (UTC, ISO 8601)", example = "2026-06-08T05:30:00Z")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        LocalDateTime startedAt,

        @Schema(description = "근무 종료 시각 (UTC, ISO 8601). IN_PROGRESS인 경우 null", example = "2026-06-08T13:45:00Z")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        LocalDateTime endedAt
) {
}
