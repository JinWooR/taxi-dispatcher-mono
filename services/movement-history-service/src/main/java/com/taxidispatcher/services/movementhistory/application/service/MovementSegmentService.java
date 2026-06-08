package com.taxidispatcher.services.movementhistory.application.service;

import com.taxidispatcher.services.movementhistory.application.dto.request.StartWorkSessionSegmentRequest;
import com.taxidispatcher.services.movementhistory.application.dto.request.UpdateSegmentPolylineRequest;
import com.taxidispatcher.services.movementhistory.application.dto.response.DispatchMovementsResponse;
import com.taxidispatcher.services.movementhistory.application.dto.response.DriverPeriodMovementsResponse;
import com.taxidispatcher.services.movementhistory.application.dto.response.MovementSegmentResponse;
import com.taxidispatcher.services.movementhistory.application.dto.response.WorkSessionMovementsResponse;
import com.taxidispatcher.services.movementhistory.domain.segment.DispatchId;
import com.taxidispatcher.services.movementhistory.domain.segment.DriverId;
import com.taxidispatcher.services.movementhistory.domain.segment.EncodedPolyline;
import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegment;
import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegmentRepository;
import com.taxidispatcher.services.movementhistory.domain.segment.WorkSessionId;
import com.taxidispatcher.shared.common.exception.DomainException;
import com.taxidispatcher.shared.common.jwt.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MovementSegmentService {

    private final MovementSegmentRepository repository;

    public MovementSegmentResponse start(String workSessionId, String driverId,
                                          StartWorkSessionSegmentRequest request) {
        MovementSegment segment = MovementSegment.start(
            new WorkSessionId(workSessionId),
            new DriverId(driverId),
            request.getDispatchId() != null ? new DispatchId(request.getDispatchId()) : null,
            request.getSegmentNo(),
            new EncodedPolyline(request.getPolyline()),
            LocalDateTime.now()
        );
        return MovementSegmentResponse.from(repository.save(segment));
    }

    public MovementSegmentResponse updatePolyline(Long segmentId, String driverId,
                                                   UpdateSegmentPolylineRequest request) {
        MovementSegment segment = findOwned(segmentId, driverId);
        segment.updatePolyline(new EncodedPolyline(request.getPolyline()), LocalDateTime.now());
        return MovementSegmentResponse.from(repository.save(segment));
    }

    public MovementSegmentResponse complete(Long segmentId, String driverId) {
        MovementSegment segment = findOwned(segmentId, driverId);
        segment.complete(LocalDateTime.now());
        return MovementSegmentResponse.from(repository.save(segment));
    }

    @Transactional(readOnly = true)
    public WorkSessionMovementsResponse findByWorkSessionId(String workSessionId, String driverId) {
        List<MovementSegment> segments = repository.findByWorkSessionId(new WorkSessionId(workSessionId));
        if (!segments.isEmpty() && !segments.get(0).getDriverId().getValue().equals(driverId)) {
            throw forbidden("해당 근무 세션에 접근할 권한이 없습니다.");
        }
        return WorkSessionMovementsResponse.of(workSessionId, segments);
    }

    @Transactional(readOnly = true)
    public DispatchMovementsResponse findByDispatchId(String dispatchId, AuthUser authUser) {
        List<MovementSegment> segments = repository.findByDispatchId(new DispatchId(dispatchId));
        if (authUser.isDriver() && !segments.isEmpty()
                && !segments.get(0).getDriverId().getValue().equals(authUser.getActor())) {
            throw forbidden("해당 배차의 이동 이력에 접근할 권한이 없습니다.");
        }
        return DispatchMovementsResponse.of(dispatchId, segments);
    }

    @Transactional(readOnly = true)
    public DriverPeriodMovementsResponse findMyPeriodMovements(String driverId,
                                                                LocalDateTime from,
                                                                LocalDateTime to) {
        if (from == null || to == null) {
            throw new DomainException(
                "INVALID_REQUEST",
                "startDate, endDate 는 모두 필수입니다.",
                HttpStatus.BAD_REQUEST
            );
        }
        List<MovementSegment> segments =
            repository.findByDriverIdAndStartedAtBetween(new DriverId(driverId), from, to);
        return DriverPeriodMovementsResponse.of(driverId, from, to, segments);
    }

    @Transactional(readOnly = true)
    public MovementSegmentResponse findMyActiveSegment(String driverId) {
        return repository.findActiveByDriverId(new DriverId(driverId))
            .map(MovementSegmentResponse::from)
            .orElse(null);
    }

    private MovementSegment findOwned(Long segmentId, String driverId) {
        MovementSegment segment = repository.findById(segmentId)
            .orElseThrow(() -> new DomainException(
                "MOVEMENT_SEGMENT_NOT_FOUND",
                String.format("Segment 를 찾을 수 없습니다: %d", segmentId),
                HttpStatus.NOT_FOUND
            ));
        if (!segment.getDriverId().getValue().equals(driverId)) {
            throw forbidden("해당 segment 에 접근할 권한이 없습니다.");
        }
        return segment;
    }

    private DomainException forbidden(String message) {
        return new DomainException("FORBIDDEN", message, HttpStatus.FORBIDDEN);
    }
}
