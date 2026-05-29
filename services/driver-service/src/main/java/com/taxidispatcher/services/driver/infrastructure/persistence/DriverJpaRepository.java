package com.taxidispatcher.services.driver.infrastructure.persistence;

import com.taxidispatcher.services.driver.domain.driver.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverJpaRepository extends JpaRepository<DriverJpaEntity, Long> {
    Optional<DriverJpaEntity> findByDriverId(String driverId);

    Optional<DriverJpaEntity> findByAccountId(String accountId);

    List<DriverJpaEntity> findByStatus(DriverStatus status);

    /**
     * 반경 내 ONLINE 기사 조회 (단위: km)
     * 1차: Bounding Box로 후보군 축소 (인덱스 활용)
     * 2차: Haversine 공식으로 정확한 원형 반경 보정
     * 6371은 지구 반지름(km)
     */
    @Query(value = """
            SELECT *,
                   (6371 * acos(
                       cos(radians(:latitude)) * cos(radians(latitude))
                       * cos(radians(longitude) - radians(:longitude))
                       + sin(radians(:latitude)) * sin(radians(latitude))
                   )) AS distance_km
            FROM drivers
            WHERE status = 'ONLINE'
              AND latitude  BETWEEN :minLatitude  AND :maxLatitude
              AND longitude BETWEEN :minLongitude AND :maxLongitude
              AND (:hasExcludes = 0 OR driver_id NOT IN (:excludeDriverIds))
            HAVING distance_km <= :radiusKm
            ORDER BY distance_km ASC
            """, nativeQuery = true)
    List<DriverJpaEntity> findOnlineDriversWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm,
            @Param("minLatitude") double minLatitude,
            @Param("maxLatitude") double maxLatitude,
            @Param("minLongitude") double minLongitude,
            @Param("maxLongitude") double maxLongitude,
            @Param("hasExcludes") int hasExcludes,
            @Param("excludeDriverIds") List<String> excludeDriverIds
    );

    boolean existsByDriverId(String driverId);

    boolean existsByAccountId(String accountId);

    boolean existsByLicenseNumber(String licenseNumber);
}
