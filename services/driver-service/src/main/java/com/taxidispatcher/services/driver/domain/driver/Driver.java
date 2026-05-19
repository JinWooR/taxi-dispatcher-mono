package com.taxidispatcher.services.driver.domain.driver;

import java.time.LocalDateTime;
import java.util.Objects;

public class Driver {
    private DriverId driverId;
    private String accountId;
    private String name;
    private String phoneNumber;
    private String licenseNumber;
    private Vehicle vehicle;
    private DriverStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Driver(DriverId driverId, String accountId, String name, String phoneNumber,
                  String licenseNumber, Vehicle vehicle) {
        this.driverId = Objects.requireNonNull(driverId);
        this.accountId = Objects.requireNonNull(accountId);
        this.name = Objects.requireNonNull(name);
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.vehicle = Objects.requireNonNull(vehicle);
        this.status = DriverStatus.OFFLINE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected Driver() {
    }

    public DriverId getDriverId() {
        return driverId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateProfile(String name, String phoneNumber, String licenseNumber, Vehicle vehicle) {
        this.name = Objects.requireNonNull(name);
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.vehicle = Objects.requireNonNull(vehicle);
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(DriverStatus newStatus) {
        Objects.requireNonNull(newStatus);
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOnline() {
        return this.status == DriverStatus.ONLINE;
    }

    public boolean isBusy() {
        return this.status == DriverStatus.BUSY;
    }

    public boolean isOffline() {
        return this.status == DriverStatus.OFFLINE;
    }

    // JPA용 setter 메서드들
    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return Objects.equals(driverId, driver.driverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId);
    }
}
