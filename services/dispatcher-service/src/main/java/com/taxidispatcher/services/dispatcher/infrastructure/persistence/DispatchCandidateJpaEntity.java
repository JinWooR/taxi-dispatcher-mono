package com.taxidispatcher.services.dispatcher.infrastructure.persistence;

import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidate;
import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidateId;
import com.taxidispatcher.services.dispatcher.domain.candidate.DispatchCandidateStatus;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchId;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DriverId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_candidates",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_dispatch_driver", columnNames = {"dispatch_id", "driver_id"})
    },
    indexes = {
        @Index(name = "idx_dispatch_id", columnList = "dispatch_id"),
        @Index(name = "idx_driver_id", columnList = "driver_id"),
        @Index(name = "idx_status", columnList = "status")
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchCandidateJpaEntity {

    @Id
    @Column(name = "candidate_id", columnDefinition = "VARCHAR(36)")
    private String candidateId;

    @Column(name = "dispatch_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String dispatchId;

    @Column(name = "driver_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String driverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DispatchCandidateStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static DispatchCandidateJpaEntity from(DispatchCandidate candidate) {
        return DispatchCandidateJpaEntity.builder()
            .candidateId(candidate.getCandidateId().getValue())
            .dispatchId(candidate.getDispatchId().getValue())
            .driverId(candidate.getDriverId().getValue())
            .status(candidate.getStatus())
            .createdAt(candidate.getCreatedAt())
            .updatedAt(candidate.getUpdatedAt())
            .build();
    }

    public DispatchCandidate toModel() {
        return DispatchCandidate.reconstitute(
            new DispatchCandidateId(candidateId),
            new DispatchId(dispatchId),
            new DriverId(driverId),
            status,
            createdAt,
            updatedAt
        );
    }
}
