package com.taxidispatcher.services.movementhistory.infrastructure.persistence;

import com.taxidispatcher.services.movementhistory.domain.segment.DispatchId;
import com.taxidispatcher.services.movementhistory.domain.segment.DriverId;
import com.taxidispatcher.services.movementhistory.domain.segment.EncodedPolyline;
import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegment;
import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegmentStatus;
import com.taxidispatcher.services.movementhistory.domain.segment.WorkSessionId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "movement_segments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_session_segment", columnNames = {"work_session_id", "segment_no"})
    },
    indexes = {
        @Index(name = "idx_driver_started", columnList = "driver_id, started_at"),
        @Index(name = "idx_dispatch_id", columnList = "dispatch_id"),
        @Index(name = "idx_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementSegmentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "work_session_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String workSessionId;

    @Column(name = "driver_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String driverId;

    @Column(name = "dispatch_id", columnDefinition = "VARCHAR(36)")
    private String dispatchId;

    @Column(name = "segment_no", nullable = false)
    private int segmentNo;

    @Column(name = "polyline", nullable = false, columnDefinition = "TEXT")
    private String polyline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MovementSegmentStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static MovementSegmentJpaEntity from(MovementSegment segment) {
        return MovementSegmentJpaEntity.builder()
            .id(segment.getId())
            .workSessionId(segment.getWorkSessionId().getValue())
            .driverId(segment.getDriverId().getValue())
            .dispatchId(segment.getDispatchId() != null ? segment.getDispatchId().getValue() : null)
            .segmentNo(segment.getSegmentNo())
            .polyline(segment.getPolyline().getValue())
            .status(segment.getStatus())
            .startedAt(segment.getStartedAt())
            .endedAt(segment.getEndedAt())
            .updatedAt(segment.getUpdatedAt())
            .build();
    }

    public MovementSegment toModel() {
        return MovementSegment.reconstitute(
            id,
            new WorkSessionId(workSessionId),
            new DriverId(driverId),
            dispatchId != null ? new DispatchId(dispatchId) : null,
            segmentNo,
            new EncodedPolyline(polyline),
            status,
            startedAt,
            endedAt,
            updatedAt
        );
    }
}
