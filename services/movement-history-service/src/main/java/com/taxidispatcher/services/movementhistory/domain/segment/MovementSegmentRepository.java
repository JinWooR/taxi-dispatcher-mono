package com.taxidispatcher.services.movementhistory.domain.segment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovementSegmentRepository {

    MovementSegment save(MovementSegment segment);

    Optional<MovementSegment> findById(Long id);

    List<MovementSegment> findByWorkSessionId(WorkSessionId workSessionId);

    List<MovementSegment> findByDispatchId(DispatchId dispatchId);

    List<MovementSegment> findByDriverIdAndStartedAtBetween(DriverId driverId,
                                                             LocalDateTime from,
                                                             LocalDateTime to);

    Optional<MovementSegment> findActiveByDriverId(DriverId driverId);
}
