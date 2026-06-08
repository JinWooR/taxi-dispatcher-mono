package com.taxidispatcher.services.movementhistory.infrastructure.persistence;

import com.taxidispatcher.services.movementhistory.domain.segment.DispatchId;
import com.taxidispatcher.services.movementhistory.domain.segment.DriverId;
import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegment;
import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegmentRepository;
import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegmentStatus;
import com.taxidispatcher.services.movementhistory.domain.segment.WorkSessionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MovementSegmentRepositoryImpl implements MovementSegmentRepository {

    private final MovementSegmentJpaRepository jpaRepository;

    @Override
    public MovementSegment save(MovementSegment segment) {
        MovementSegmentJpaEntity entity = MovementSegmentJpaEntity.from(segment);
        MovementSegmentJpaEntity saved = jpaRepository.save(entity);
        return saved.toModel();
    }

    @Override
    public Optional<MovementSegment> findById(Long id) {
        return jpaRepository.findById(id).map(MovementSegmentJpaEntity::toModel);
    }

    @Override
    public List<MovementSegment> findByWorkSessionId(WorkSessionId workSessionId) {
        return jpaRepository.findByWorkSessionIdOrderBySegmentNoAsc(workSessionId.getValue())
            .stream()
            .map(MovementSegmentJpaEntity::toModel)
            .toList();
    }

    @Override
    public List<MovementSegment> findByDispatchId(DispatchId dispatchId) {
        return jpaRepository.findByDispatchIdOrderBySegmentNoAsc(dispatchId.getValue())
            .stream()
            .map(MovementSegmentJpaEntity::toModel)
            .toList();
    }

    @Override
    public List<MovementSegment> findByDriverIdAndStartedAtBetween(DriverId driverId,
                                                                    LocalDateTime from,
                                                                    LocalDateTime to) {
        return jpaRepository.findByDriverIdAndStartedAtBetweenOrderByStartedAtAsc(
                driverId.getValue(), from, to)
            .stream()
            .map(MovementSegmentJpaEntity::toModel)
            .toList();
    }

    @Override
    public Optional<MovementSegment> findActiveByDriverId(DriverId driverId) {
        return jpaRepository.findFirstByDriverIdAndStatusOrderByStartedAtDesc(
                driverId.getValue(), MovementSegmentStatus.IN_PROGRESS)
            .map(MovementSegmentJpaEntity::toModel);
    }
}
