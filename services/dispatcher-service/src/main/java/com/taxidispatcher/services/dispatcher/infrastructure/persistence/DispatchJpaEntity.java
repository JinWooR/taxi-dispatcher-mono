package com.taxidispatcher.services.dispatcher.infrastructure.persistence;

import com.taxidispatcher.services.dispatcher.domain.dispatch.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispatches", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_driver_id", columnList = "driver_id"),
    @Index(name = "idx_status", columnList = "dispatch_status"),
    @Index(name = "idx_requested_at", columnList = "requested_at")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchJpaEntity {

    @Id
    @Column(name = "dispatch_id", columnDefinition = "VARCHAR(36)")
    private String dispatchId;

    @Column(name = "user_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String userId;

    @Column(name = "driver_id", columnDefinition = "VARCHAR(36)")
    private String driverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispatch_status", nullable = false, length = 20)
    private DispatchStatus dispatchStatus;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "latitude", column = @Column(name = "departure_latitude", nullable = false)),
        @AttributeOverride(name = "longitude", column = @Column(name = "departure_longitude", nullable = false)),
        @AttributeOverride(name = "address", column = @Column(name = "departure_address", nullable = false))
    })
    private LocationEmbedded departure;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "latitude", column = @Column(name = "arrival_latitude", nullable = false)),
        @AttributeOverride(name = "longitude", column = @Column(name = "arrival_longitude", nullable = false)),
        @AttributeOverride(name = "address", column = @Column(name = "arrival_address", nullable = false))
    })
    private LocationEmbedded arrival;

    @Column(name = "current_scope", nullable = false)
    private int currentScope;

    @Column(name = "scope_started_at", nullable = false)
    private LocalDateTime scopeStartedAt;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "departed_at")
    private LocalDateTime departedAt;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public static DispatchJpaEntity from(Dispatch dispatch) {
        return DispatchJpaEntity.builder()
            .dispatchId(dispatch.getDispatchId().getValue())
            .userId(dispatch.getUserId().getValue())
            .driverId(dispatch.getDriverId() != null ? dispatch.getDriverId().getValue() : null)
            .dispatchStatus(dispatch.getDispatchStatus())
            .departure(LocationEmbedded.from(dispatch.getDeparture()))
            .arrival(LocationEmbedded.from(dispatch.getArrival()))
            .currentScope(dispatch.getSearchScope().getCurrentScope())
            .scopeStartedAt(dispatch.getSearchScope().getScopeStartedAt())
            .requestedAt(dispatch.getRequestedAt())
            .failedAt(dispatch.getFailedAt())
            .approvedAt(dispatch.getApprovedAt())
            .departedAt(dispatch.getDepartedAt())
            .arrivedAt(dispatch.getArrivedAt())
            .completedAt(dispatch.getCompletedAt())
            .build();
    }

    public Dispatch toModel() {
        return Dispatch.reconstitute(
            new DispatchId(dispatchId),
            new UserId(userId),
            driverId != null ? new DriverId(driverId) : null,
            dispatchStatus,
            departure.toModel(),
            arrival.toModel(),
            SearchScope.reconstitute(currentScope, scopeStartedAt),
            requestedAt,
            failedAt,
            approvedAt,
            departedAt,
            arrivedAt,
            completedAt
        );
    }
}
