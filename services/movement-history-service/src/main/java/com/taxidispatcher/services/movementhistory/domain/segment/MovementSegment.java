package com.taxidispatcher.services.movementhistory.domain.segment;

import com.taxidispatcher.shared.common.exception.DomainException;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MovementSegment {

    private final Long id;
    private final WorkSessionId workSessionId;
    private final DriverId driverId;
    private final DispatchId dispatchId;
    private final int segmentNo;
    private final LocalDateTime startedAt;

    private EncodedPolyline polyline;
    private MovementSegmentStatus status;
    private LocalDateTime endedAt;
    private LocalDateTime updatedAt;

    private MovementSegment(
        WorkSessionId workSessionId,
        DriverId driverId,
        DispatchId dispatchId,
        int segmentNo,
        EncodedPolyline polyline,
        LocalDateTime startedAt
    ) {
        this.id = null;
        this.workSessionId = workSessionId;
        this.driverId = driverId;
        this.dispatchId = dispatchId;
        this.segmentNo = segmentNo;
        this.polyline = polyline;
        this.status = MovementSegmentStatus.IN_PROGRESS;
        this.startedAt = startedAt;
        this.endedAt = null;
        this.updatedAt = startedAt;
    }

    // DB 복원용
    private MovementSegment(
        Long id,
        WorkSessionId workSessionId,
        DriverId driverId,
        DispatchId dispatchId,
        int segmentNo,
        EncodedPolyline polyline,
        MovementSegmentStatus status,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.workSessionId = workSessionId;
        this.driverId = driverId;
        this.dispatchId = dispatchId;
        this.segmentNo = segmentNo;
        this.polyline = polyline;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.updatedAt = updatedAt;
    }

    public static MovementSegment start(
        WorkSessionId workSessionId,
        DriverId driverId,
        DispatchId dispatchId,
        int segmentNo,
        EncodedPolyline polyline,
        LocalDateTime now
    ) {
        return new MovementSegment(workSessionId, driverId, dispatchId, segmentNo, polyline, now);
    }

    public static MovementSegment reconstitute(
        Long id,
        WorkSessionId workSessionId,
        DriverId driverId,
        DispatchId dispatchId,
        int segmentNo,
        EncodedPolyline polyline,
        MovementSegmentStatus status,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        LocalDateTime updatedAt
    ) {
        return new MovementSegment(
            id, workSessionId, driverId, dispatchId, segmentNo,
            polyline, status, startedAt, endedAt, updatedAt
        );
    }

    public void updatePolyline(EncodedPolyline polyline, LocalDateTime now) {
        if (status == MovementSegmentStatus.COMPLETED) {
            throw new DomainException(
                "MOVEMENT_SEGMENT_COMPLETED_NOT_EDITABLE",
                "완료된 segment 의 polyline 은 수정할 수 없습니다.",
                HttpStatus.CONFLICT
            );
        }
        this.polyline = polyline;
        this.updatedAt = now;
    }

    public void complete(LocalDateTime now) {
        if (!status.canTransitionTo(MovementSegmentStatus.COMPLETED)) {
            throw new DomainException(
                "MOVEMENT_SEGMENT_INVALID_TRANSITION",
                String.format("%s 상태에서 %s 로 전이할 수 없습니다.",
                    status.getDisplayName(),
                    MovementSegmentStatus.COMPLETED.getDisplayName()),
                HttpStatus.CONFLICT
            );
        }
        this.status = MovementSegmentStatus.COMPLETED;
        this.endedAt = now;
        this.updatedAt = now;
    }
}
