package com.taxidispatcher.services.driver.infrastructure.persistence;

import com.taxidispatcher.services.driver.domain.driver.Driver;
import com.taxidispatcher.services.driver.domain.driver.DriverId;
import com.taxidispatcher.services.driver.domain.driver.DriverRepository;
import com.taxidispatcher.services.driver.domain.driver.DriverStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverRepositoryImpl implements DriverRepository {

    private final DriverJpaRepository jpaRepository;

    @Override
    public Driver save(Driver driver) {
        DriverJpaEntity entity = DriverJpaEntity.fromDomain(driver);

        Optional<DriverJpaEntity> existing = jpaRepository.findByDriverId(driver.getDriverId().getValue());

        DriverJpaEntity saved;
        if (existing.isPresent()) {
            DriverJpaEntity existingEntity = existing.get();
            existingEntity.updateFromDomain(driver);
            saved = jpaRepository.save(existingEntity);
        } else {
            saved = jpaRepository.save(entity);
        }

        return saved.toDomain();
    }

    @Override
    public Optional<Driver> findById(DriverId driverId) {
        return jpaRepository.findByDriverId(driverId.getValue())
                .map(DriverJpaEntity::toDomain);
    }

    @Override
    public Optional<Driver> findByAccountId(String accountId) {
        return jpaRepository.findByAccountId(accountId)
                .map(DriverJpaEntity::toDomain);
    }

    @Override
    public List<Driver> findByStatus(DriverStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(DriverJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Driver> findOnlineDriversWithinRadius(
            double latitude, double longitude, double radiusKm,
            double minLatitude, double maxLatitude,
            double minLongitude, double maxLongitude,
            List<String> excludeDriverIds) {

        // NOT IN ()는 SQL 오류 → 빈 리스트일 때 더미 값으로 대체 + hasExcludes 플래그로 분기
        boolean hasExcludes = excludeDriverIds != null && !excludeDriverIds.isEmpty();
        List<String> effectiveExcludes = hasExcludes ? excludeDriverIds : List.of("");

        return jpaRepository.findOnlineDriversWithinRadius(
                        latitude, longitude, radiusKm,
                        minLatitude, maxLatitude,
                        minLongitude, maxLongitude,
                        hasExcludes ? 1 : 0,
                        effectiveExcludes)
                .stream()
                .map(DriverJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByDriverId(DriverId driverId) {
        return jpaRepository.existsByDriverId(driverId.getValue());
    }

    @Override
    public boolean existsByAccountId(String accountId) {
        return jpaRepository.existsByAccountId(accountId);
    }

    @Override
    public boolean existsByLicenseNumber(String licenseNumber) {
        return jpaRepository.existsByLicenseNumber(licenseNumber);
    }
}
