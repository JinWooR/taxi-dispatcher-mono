package com.taxidispatcher.services.movementhistory.application.service;

import com.taxidispatcher.services.movementhistory.application.dto.request.RotateSegmentRequest;
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
import com.taxidispatcher.services.movementhistory.infrastructure.client.DriverServiceClient;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalWorkSession;
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

    private static final String WORK_SESSION_STATUS_IN_PROGRESS = "IN_PROGRESS";

    private final MovementSegmentRepository repository;
    private final DriverServiceClient driverServiceClient;

    public MovementSegmentResponse start(String workSessionId, String driverId,
                                          StartWorkSessionSegmentRequest request) {
        return startInternal(workSessionId, driverId, request.getPolyline(), request.getDispatchId());
    }

    public MovementSegmentResponse rotate(String workSessionId, String driverId,
                                           RotateSegmentRequest request) {
        WorkSessionId workSession = new WorkSessionId(workSessionId);
        LocalDateTime now = LocalDateTime.now();

        repository.findActiveByWorkSessionId(workSession)
            .ifPresent(active -> {
                if (!active.getDriverId().getValue().equals(driverId)) {
                    throw forbidden("해당 근무 세션의 활성 segment 에 접근할 권한이 없습니다.");
                }
                if (request.current() != null && request.current().getPolyline() != null) {
                    active.updatePolyline(new EncodedPolyline(request.current().getPolyline()), now);
                }
                active.complete(now);
                repository.save(active);
            });

        StartWorkSessionSegmentRequest next = request.next();
        return startInternal(workSessionId, driverId, next.getPolyline(), next.getDispatchId());
    }

    private MovementSegmentResponse startInternal(String workSessionId, String driverId,
                                                   String polyline, String dispatchId) {
        verifyWorkSession(workSessionId, driverId);

        WorkSessionId workSession = new WorkSessionId(workSessionId);
        int nextSegmentNo = repository.countByWorkSessionId(workSession) + 1;
        MovementSegment segment = MovementSegment.start(
            workSession,
            new DriverId(driverId),
            dispatchId != null ? new DispatchId(dispatchId) : null,
            nextSegmentNo,
            new EncodedPolyline(polyline),
            LocalDateTime.now()
        );
        return MovementSegmentResponse.from(repository.save(segment));
    }

    /**
     * 새 segment 생성 직전 driver-service 의 work_session 유효성 검증.
     * - 존재 여부 (404)
     * - 토큰 driverId 와 work_session.driverId 일치 (403)
     * - status == IN_PROGRESS (ENDED 면 409)
     */
    private void verifyWorkSession(String workSessionId, String driverId) {
        DriverInternalWorkSession ws = driverServiceClient.findWorkSession(workSessionId)
            .orElseThrow(() -> new DomainException(
                "MOVEMENT_WORK_SESSION_NOT_FOUND",
                String.format("근무 세션을 찾을 수 없습니다: %s", workSessionId),
                HttpStatus.NOT_FOUND
            ));
        if (!ws.driverId().equals(driverId)) {
            throw forbidden("해당 근무 세션에 접근할 권한이 없습니다.");
        }
        if (!WORK_SESSION_STATUS_IN_PROGRESS.equals(ws.status())) {
            throw new DomainException(
                "MOVEMENT_WORK_SESSION_NOT_IN_PROGRESS",
                "종료된 근무 세션에는 segment 를 생성할 수 없습니다.",
                HttpStatus.CONFLICT
            );
        }
    }

    public MovementSegmentResponse updatePolyline(Long segmentId, String driverId,
                                                   UpdateSegmentPolylineRequest request) {
        MovementSegment segment = findOwned(segmentId, driverId);
        segment.updatePolyline(new EncodedPolyline(request.getPolyline()), LocalDateTime.now());
        return MovementSegmentResponse.from(repository.save(segment));
    }

    public MovementSegmentResponse complete(String workSessionId, String driverId) {
        MovementSegment segment = repository.findActiveByWorkSessionId(new WorkSessionId(workSessionId))
            .orElseThrow(() -> new DomainException(
                "MOVEMENT_NO_ACTIVE_SEGMENT",
                "근무 세션에 진행 중 segment 가 없습니다.",
                HttpStatus.NOT_FOUND
            ));
        if (!segment.getDriverId().getValue().equals(driverId)) {
            throw forbidden("해당 근무 세션의 활성 segment 에 접근할 권한이 없습니다.");
        }
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
