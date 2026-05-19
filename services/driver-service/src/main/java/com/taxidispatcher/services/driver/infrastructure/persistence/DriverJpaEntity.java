package com.taxidispatcher.services.driver.infrastructure.persistence;

import com.taxidispatcher.services.driver.domain.driver.Driver;
import com.taxidispatcher.services.driver.domain.driver.DriverId;
import com.taxidispatcher.services.driver.domain.driver.DriverStatus;
import com.taxidispatcher.services.driver.domain.driver.Vehicle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "drivers", indexes = {
        @Index(name = "idx_driver_id", columnList = "driver_id", unique = true),
        @Index(name = "idx_account_id", columnList = "account_id", unique = true),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 36, nullable = false)
    private String driverId;

    @Column(unique = true, length = 36, nullable = false)
    private String accountId;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 50)
    private String licenseNumber;

    @Column(length = 20)
    private String plateNumber;

    @Column(length = 50)
    private String vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DriverStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Driver toDomain() {
        Driver driver = new Driver(
                DriverId.of(this.driverId),
                this.accountId,
                this.name,
                this.phoneNumber,
                this.licenseNumber,
                Vehicle.of(this.plateNumber, this.vehicleType)
        );
        driver.setStatus(this.status);
        driver.setCreatedAt(this.createdAt);
        driver.setUpdatedAt(this.updatedAt);
        return driver;
    }

    public static DriverJpaEntity fromDomain(Driver driver) {
        return DriverJpaEntity.builder()
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

    public void updateFromDomain(Driver driver) {
        this.accountId = driver.getAccountId();
        this.name = driver.getName();
        this.phoneNumber = driver.getPhoneNumber();
        this.licenseNumber = driver.getLicenseNumber();
        this.plateNumber = driver.getVehicle().getPlateNumber();
        this.vehicleType = driver.getVehicle().getVehicleType();
        this.status = driver.getStatus();
        this.updatedAt = driver.getUpdatedAt();
    }
}
