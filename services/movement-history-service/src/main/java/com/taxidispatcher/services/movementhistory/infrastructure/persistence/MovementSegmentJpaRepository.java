package com.taxidispatcher.services.movementhistory.infrastructure.persistence;

import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovementSegmentJpaRepository extends JpaRepository<MovementSegmentJpaEntity, Long> {

    List<MovementSegmentJpaEntity> findByWorkSessionIdOrderBySegmentNoAsc(String workSessionId);

    long countByWorkSessionId(String workSessionId);

    Optional<MovementSegmentJpaEntity> findFirstByWorkSessionIdAndStatusOrderBySegmentNoDesc(
            String workSessionId, MovementSegmentStatus status);

    List<MovementSegmentJpaEntity> findByDispatchIdOrderBySegmentNoAsc(String dispatchId);

    List<MovementSegmentJpaEntity> findByDriverIdAndStartedAtBetweenOrderByStartedAtAsc(
            String driverId, LocalDateTime from, LocalDateTime to);

    Optional<MovementSegmentJpaEntity> findFirstByDriverIdAndStatusOrderByStartedAtDesc(
            String driverId, MovementSegmentStatus status);
}
