package com.taxidispatcher.services.driver.application.dto.response;

import com.taxidispatcher.services.driver.domain.driver.Driver;
import com.taxidispatcher.services.driver.domain.driver.DriverStatus;
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
    private String driverId;
    private String accountId;
    private String name;
    private String phoneNumber;
    private String licenseNumber;
    private String plateNumber;
    private String vehicleType;
    private DriverStatus status;
    private LocalDateTime createdAt;
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
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
