package com.taxidispatcher.services.dispatcher.domain.dispatch;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Dispatch {
    // 배차 고유 ID
    private final DispatchId dispatchId;
    // 배차 상태
    private DispatchStatus dispatchStatus;
    // 담당 기사 ID
    private DriverId driverId;
    // 출발지 정보
    private final Location departure;
    // 도착지 정보
    private final Location arrival;
    // 탐색 범위 관리
    private final SearchScope searchScope;
    // 배차 요청 시간
    private final LocalDateTime requestedAt;
    // 배차 실패 시간
    private LocalDateTime failedAt;
    // 배차 승인 시간
    private LocalDateTime approvedAt;
    // 출발 시간
    private LocalDateTime departedAt;
    // 목적지 도착 시간
    private LocalDateTime arrivedAt;
    // 배차 완료 시간
    private LocalDateTime completedAt;

    private Dispatch(DispatchId dispatchId, Location departure, Location arrival) {
        this.dispatchId = dispatchId;
        this.departure = departure;
        this.arrival = arrival;
        this.searchScope = new SearchScope();
        this.dispatchStatus = DispatchStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }

    public static Dispatch create(Location departure, Location arrival) {
        return new Dispatch(DispatchId.generate(), departure, arrival);
    }

    public static Dispatch createWithId(DispatchId dispatchId, Location departure, Location arrival) {
        return new Dispatch(dispatchId, departure, arrival);
    }

    public void assignDriver(DriverId driverId) {
        this.driverId = driverId;
    }

    public void updateStatus(DispatchStatus nextStatus) {
        if (!dispatchStatus.canTransitionTo(nextStatus)) {
            throw new IllegalStateException(
                String.format("%s 상태에서 %s로 전이할 수 없습니다.",
                    dispatchStatus.getDisplayName(), nextStatus.getDisplayName())
            );
        }

        if (nextStatus == DispatchStatus.ASSIGNED && driverId == null) {
            throw new IllegalStateException("배차 승인시 기사 ID가 필수입니다.");
        }

        this.dispatchStatus = nextStatus;
        updateTimestampByStatus(nextStatus);
    }

    private void updateTimestampByStatus(DispatchStatus status) {
        switch (status) {
            case ASSIGNED -> this.approvedAt = LocalDateTime.now();
            case FAILED -> this.failedAt = LocalDateTime.now();
            case IN_PROGRESS -> this.departedAt = LocalDateTime.now();
            case ARRIVED -> this.arrivedAt = LocalDateTime.now();
            case COMPLETED -> this.completedAt = LocalDateTime.now();
            case CANCELLED -> {} // timestamp 없음
            case REQUESTED -> {} // 초기 상태, 이미 requestedAt이 설정됨
        }
    }
}
