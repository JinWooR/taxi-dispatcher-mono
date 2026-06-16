package com.taxidispatcher.services.driver.domain.worksession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface WorkSessionRepository {
    WorkSession save(WorkSession workSession);

    Optional<WorkSession> findById(WorkSessionId workSessionId);

    Optional<WorkSession> findInProgressByDriverId(String driverId);

    Page<WorkSession> findByDriverId(String driverId, Pageable pageable);
}
