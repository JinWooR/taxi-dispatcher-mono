package com.taxidispatcher.services.driver.application.service;

import com.taxidispatcher.services.driver.application.dto.request.RegisterDriverRequest;
import com.taxidispatcher.services.driver.application.dto.request.UpdateDriverRequest;
import com.taxidispatcher.services.driver.domain.driver.Driver;
import com.taxidispatcher.services.driver.domain.driver.DriverId;
import com.taxidispatcher.services.driver.domain.driver.DriverRepository;
import com.taxidispatcher.services.driver.domain.driver.DriverStatus;
import com.taxidispatcher.services.driver.domain.driver.Vehicle;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalProfile;
import com.taxidispatcher.shared.common.exception.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;

    public Driver registerDriver(String accountId, RegisterDriverRequest request) {
        if (driverRepository.existsByAccountId(accountId)) {
            throw new DomainException("DRIVER_DUPLICATE", "이미 등록된 기사입니다", HttpStatus.CONFLICT);
        }

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DomainException("DRIVER_INVALID_LICENSE", "이미 등록된 면허번호입니다", HttpStatus.CONFLICT);
        }

        DriverId driverId = DriverId.generate();
        Vehicle vehicle = Vehicle.of(request.getPlateNumber(), request.getVehicleType());
        Driver driver = new Driver(
                driverId,
                accountId,
                request.getName(),
                request.getPhoneNumber(),
                request.getLicenseNumber(),
                vehicle
        );

        return driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public Driver getDriverByAccountId(String accountId) {
        return driverRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DomainException("DRIVER_NOT_FOUND", "기사를 찾을 수 없습니다", HttpStatus.NOT_FOUND));
    }

    /**
     * 내부 API: accountId로 기사 프로필 조회 (서비스 간 통신용)
     */
    @Transactional(readOnly = true)
    public DriverInternalProfile findProfileByAccountId(String accountId) {
        Driver driver = driverRepository.findByAccountId(accountId)
                .orElseThrow(() -> new DomainException("DRIVER_NOT_FOUND", "기사를 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        return DriverInternalProfile.builder()
                .driverId(driver.getDriverId().getValue())
                .accountId(driver.getAccountId())
                .name(driver.getName())
                .phoneNumber(driver.getPhoneNumber())
                .licenseNumber(driver.getLicenseNumber())
                .plateNumber(driver.getVehicle().getPlateNumber())
                .vehicleType(driver.getVehicle().getVehicleType())
                .status(driver.getStatus().name())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Driver getDriver(DriverId driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new DomainException("DRIVER_NOT_FOUND", "기사를 찾을 수 없습니다", HttpStatus.NOT_FOUND));
    }

    public Driver updateDriver(String accountId, UpdateDriverRequest request) {
        Driver driver = getDriverByAccountId(accountId);

        if (!driver.getLicenseNumber().equals(request.getLicenseNumber())
                && driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DomainException("DRIVER_INVALID_LICENSE", "이미 등록된 면허번호입니다", HttpStatus.CONFLICT);
        }

        Vehicle vehicle = Vehicle.of(request.getPlateNumber(), request.getVehicleType());
        driver.updateProfile(
                request.getName(),
                request.getPhoneNumber(),
                request.getLicenseNumber(),
                vehicle
        );

        return driverRepository.save(driver);
    }

    public void changeStatus(String accountId, DriverStatus newStatus) {
        Driver driver = getDriverByAccountId(accountId);
        driver.changeStatus(newStatus);
        driverRepository.save(driver);
        log.info("기사 상태 변경: accountId={}, status={}", accountId, newStatus);
    }

    @Transactional(readOnly = true)
    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByStatus(DriverStatus.ONLINE);
    }

    @Transactional(readOnly = true)
    public List<Driver> getDriversByStatus(DriverStatus status) {
        return driverRepository.findByStatus(status);
    }
}
