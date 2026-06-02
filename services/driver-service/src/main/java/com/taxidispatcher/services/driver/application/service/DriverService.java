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

        return toInternalProfile(driver);
    }

    /**
     * 내부 API: driverId로 기사 프로필 조회 (서비스 간 통신용)
     */
    @Transactional(readOnly = true)
    public DriverInternalProfile findProfileByDriverId(String driverId) {
        Driver driver = driverRepository.findById(DriverId.of(driverId))
                .orElseThrow(() -> new DomainException("DRIVER_NOT_FOUND", "기사를 찾을 수 없습니다", HttpStatus.NOT_FOUND));

        return toInternalProfile(driver);
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

    public void updateLocation(String accountId, double latitude, double longitude) {
        Driver driver = getDriverByAccountId(accountId);
        driver.updateLocation(latitude, longitude);
        driverRepository.save(driver);
    }

    /**
     * 내부 API: 특정 좌표 기준 반경 내 ONLINE 기사 조회
     * Bounding Box를 계산하여 후보군 사전 축소
     */
    @Transactional(readOnly = true)
    public List<DriverInternalProfile> findNearbyDrivers(
            double latitude, double longitude, double radiusKm,
            List<String> excludeDriverIds) {

        // Bounding Box 좌표 계산
        // 위도 1도 ≈ 111km, 경도 1도 ≈ 111km * cos(위도)
        double latDelta = radiusKm / 111.0;
        double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));

        double minLatitude = latitude - latDelta;
        double maxLatitude = latitude + latDelta;
        double minLongitude = longitude - lngDelta;
        double maxLongitude = longitude + lngDelta;

        return driverRepository.findOnlineDriversWithinRadius(
                        latitude, longitude, radiusKm,
                        minLatitude, maxLatitude,
                        minLongitude, maxLongitude,
                        excludeDriverIds)
                .stream()
                .map(this::toInternalProfile)
                .toList();
    }

    private DriverInternalProfile toInternalProfile(Driver driver) {
        return DriverInternalProfile.builder()
                .driverId(driver.getDriverId().getValue())
                .accountId(driver.getAccountId())
                .name(driver.getName())
                .phoneNumber(driver.getPhoneNumber())
                .licenseNumber(driver.getLicenseNumber())
                .plateNumber(driver.getVehicle().getPlateNumber())
                .vehicleType(driver.getVehicle().getVehicleType())
                .status(driver.getStatus().name())
                .latitude(driver.getLocation() != null ? driver.getLocation().getLatitude() : null)
                .longitude(driver.getLocation() != null ? driver.getLocation().getLongitude() : null)
                .locationUpdatedAt(driver.getLocation() != null ? driver.getLocation().getUpdatedAt() : null)
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
