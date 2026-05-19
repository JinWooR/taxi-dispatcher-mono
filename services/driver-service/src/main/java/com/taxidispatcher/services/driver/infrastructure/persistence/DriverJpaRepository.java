package com.taxidispatcher.services.driver.infrastructure.persistence;

import com.taxidispatcher.services.driver.domain.driver.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverJpaRepository extends JpaRepository<DriverJpaEntity, Long> {
    Optional<DriverJpaEntity> findByDriverId(String driverId);

    Optional<DriverJpaEntity> findByAccountId(String accountId);

    List<DriverJpaEntity> findByStatus(DriverStatus status);

    boolean existsByDriverId(String driverId);

    boolean existsByAccountId(String accountId);

    boolean existsByLicenseNumber(String licenseNumber);
}
