package com.taxidispatcher.services.driver.domain.driver;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {
    Driver save(Driver driver);

    Optional<Driver> findById(DriverId driverId);

    Optional<Driver> findByAccountId(String accountId);

    List<Driver> findByStatus(DriverStatus status);

    boolean existsByDriverId(DriverId driverId);

    boolean existsByAccountId(String accountId);

    boolean existsByLicenseNumber(String licenseNumber);
}
