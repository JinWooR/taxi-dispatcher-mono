package com.taxidispatcher.services.driver.application.dto.response;

import com.taxidispatcher.services.driver.domain.driver.Driver;
import com.taxidispatcher.services.driver.domain.driver.DriverStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponse {

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

    @Schema(description = "차종", example = "SEDAN")
    private String vehicleType;

    @Schema(description = "기사 상태", example = "ONLINE")
    private DriverStatus status;

    @Schema(description = "현재 위치 - 위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "현재 위치 - 경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "위치 갱신 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
    private LocalDateTime locationUpdatedAt;

    @Schema(description = "생성 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각 (UTC, ISO 8601)", example = "2026-06-04T05:30:00Z")
    private LocalDateTime updatedAt;

    public static DriverResponse from(Driver driver) {
        return DriverResponse.builder()
                .driverId(driver.getDriverId().getValue())
                .accountId(driver.getAccountId())
                .name(driver.getName())
                .phoneNumber(driver.getPhoneNumber())
                .licenseNumber(driver.getLicenseNumber())
                .plateNumber(driver.getVehicle().getPlateNumber())
                .vehicleType(driver.getVehicle().getVehicleType())
                .status(driver.getStatus())
                .latitude(driver.getLocation() != null ? driver.getLocation().getLatitude() : null)
                .longitude(driver.getLocation() != null ? driver.getLocation().getLongitude() : null)
                .locationUpdatedAt(driver.getLocation() != null ? driver.getLocation().getUpdatedAt() : null)
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
