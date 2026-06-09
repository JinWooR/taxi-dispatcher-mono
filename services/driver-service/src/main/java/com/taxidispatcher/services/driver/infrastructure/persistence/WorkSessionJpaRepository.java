package com.taxidispatcher.services.driver.infrastructure.persistence;

import com.taxidispatcher.services.driver.domain.worksession.WorkSessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkSessionJpaRepository extends JpaRepository<WorkSessionJpaEntity, Long> {
    Optional<WorkSessionJpaEntity> findByWorkSessionId(String workSessionId);

    Optional<WorkSessionJpaEntity> findFirstByDriverIdAndStatus(String driverId, WorkSessionStatus status);

    Page<WorkSessionJpaEntity> findByDriverId(String driverId, Pageable pageable);
}
