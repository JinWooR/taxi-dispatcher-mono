package com.taxidispatcher.shared.common.dto.driver.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private String driverId;
    private String accountId;
    private String name;
    private String phoneNumber;
    private String licenseNumber;
    private String plateNumber;
    private String vehicleType;
    private String status;

    private Double latitude;
    private Double longitude;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime locationUpdatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime updatedAt;
}
