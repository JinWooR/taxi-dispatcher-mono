package com.taxidispatcher.services.driver.domain.driver;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {
    Driver save(Driver driver);

    Optional<Driver> findById(DriverId driverId);

    Optional<Driver> findByAccountId(String accountId);

    List<Driver> findByStatus(DriverStatus status);

    /**
     * 특정 좌표 기준 반경 내 ONLINE 상태 기사 조회
     * Bounding Box로 1차 필터링 후 Haversine 공식으로 정확도 보정
     *
     * @param latitude         중심 위도
     * @param longitude        중심 경도
     * @param radiusKm         반경 (km)
     * @param minLatitude      Bounding Box 최소 위도
     * @param maxLatitude      Bounding Box 최대 위도
     * @param minLongitude     Bounding Box 최소 경도
     * @param maxLongitude     Bounding Box 최대 경도
     * @param excludeDriverIds 제외할 기사 ID 목록 (null/empty 허용)
     * @return 거리 오름차순 정렬된 기사 목록
     */
    List<Driver> findOnlineDriversWithinRadius(
            double latitude, double longitude, double radiusKm,
            double minLatitude, double maxLatitude,
            double minLongitude, double maxLongitude,
            List<String> excludeDriverIds);

    boolean existsByDriverId(DriverId driverId);

    boolean existsByAccountId(String accountId);

    boolean existsByLicenseNumber(String licenseNumber);
}
