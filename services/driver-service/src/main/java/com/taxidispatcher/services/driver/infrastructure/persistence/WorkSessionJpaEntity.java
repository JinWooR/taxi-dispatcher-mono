package com.taxidispatcher.services.driver.infrastructure.persistence;

import com.taxidispatcher.services.driver.domain.worksession.WorkSession;
import com.taxidispatcher.services.driver.domain.worksession.WorkSessionId;
import com.taxidispatcher.services.driver.domain.worksession.WorkSessionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_sessions", indexes = {
        @Index(name = "idx_work_session_id", columnList = "work_session_id", unique = true),
        @Index(name = "idx_driver_status", columnList = "driver_id, status"),
        @Index(name = "idx_driver_started", columnList = "driver_id, started_at")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSessionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 36, nullable = false)
    private String workSessionId;

    @Column(length = 36, nullable = false)
    private String driverId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private WorkSessionStatus status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime endedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public WorkSession toDomain() {
        WorkSession workSession = new WorkSession(
                WorkSessionId.of(this.workSessionId),
                this.driverId,
                this.startedAt
        );
        workSession.setStatus(this.status);
        workSession.setEndedAt(this.endedAt);
        workSession.setCreatedAt(this.createdAt);
        workSession.setUpdatedAt(this.updatedAt);
        return workSession;
    }

    public static WorkSessionJpaEntity fromDomain(WorkSession workSession) {
        return WorkSessionJpaEntity.builder()
                .workSessionId(workSession.getWorkSessionId().getValue())
                .driverId(workSession.getDriverId())
                .status(workSession.getStatus())
                .startedAt(workSession.getStartedAt())
                .endedAt(workSession.getEndedAt())
                .createdAt(workSession.getCreatedAt())
                .updatedAt(workSession.getUpdatedAt())
                .build();
    }

    public void updateFromDomain(WorkSession workSession) {
        this.status = workSession.getStatus();
        this.endedAt = workSession.getEndedAt();
        this.updatedAt = workSession.getUpdatedAt();
    }
}
