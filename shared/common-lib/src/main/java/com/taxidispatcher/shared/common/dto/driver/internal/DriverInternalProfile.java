package com.taxidispatcher.shared.common.dto.driver.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 기사 프로필 내부 통신 DTO
 * driver-service의 내부 API 응답에 사용
 * 서비스 간 통신용으로 공유
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriverInternalProfile {

    @Schema(description = "기사 ID (UUID)", example = "770e8400-e29b-41d4-a716-446655440002")
    private String driverId;

    @Schema(description = "연결된 계정 ID (UUID)", example = "880e8400-e29b-41d4-a716-446655440003")
    private String accountId;

    @Schema(description = "기사 이름", example = "김기사")
    private String name;

    @Schema(description = "기사 연락처", example = "010-9876-5432")
    private String phoneNumber;

    @Schema(description = "운전면허 번호", example = "12-34-567890-12")
    private String licenseNumber;

    @Schema(description = "차량 번호판", example = "12가3456")
    private String plateNumber;

    @Schema(description = "차량 종류", example = "SEDAN")
    private String vehicleType;

    @Schema(description = "기사 상태 (ONLINE / OFFLINE / DISPATCHED 등)", example = "ONLINE")
    private String status;

    @Schema(description = "최근 위치 - 위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "최근 위치 - 경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "위치 갱신 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime locationUpdatedAt;

    @Schema(description = "생성 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime updatedAt;
}
