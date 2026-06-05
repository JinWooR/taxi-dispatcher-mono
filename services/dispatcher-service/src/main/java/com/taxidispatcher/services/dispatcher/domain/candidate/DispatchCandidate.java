package com.taxidispatcher.services.dispatcher.domain.candidate;

import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchId;
import com.taxidispatcher.services.dispatcher.domain.dispatch.DriverId;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DispatchCandidate {
    // 후보 고유 ID
    private final DispatchCandidateId candidateId;
    // 배차 ID
    private final DispatchId dispatchId;
    // 후보 기사 ID
    private final DriverId driverId;
    // 후보 상태
    private DispatchCandidateStatus status;
    // 등록 일시
    private final LocalDateTime createdAt;
    // 수정 일시
    private LocalDateTime updatedAt;

    private DispatchCandidate(DispatchCandidateId candidateId, DispatchId dispatchId, DriverId driverId) {
        this.candidateId = candidateId;
        this.dispatchId = dispatchId;
        this.driverId = driverId;
        this.status = DispatchCandidateStatus.REQUESTED;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // DB 복원용 생성자
    private DispatchCandidate(
        DispatchCandidateId candidateId,
        DispatchId dispatchId,
        DriverId driverId,
        DispatchCandidateStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.candidateId = candidateId;
        this.dispatchId = dispatchId;
        this.driverId = driverId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static DispatchCandidate create(DispatchId dispatchId, DriverId driverId) {
        return new DispatchCandidate(DispatchCandidateId.generate(), dispatchId, driverId);
    }

    public static DispatchCandidate reconstitute(
        DispatchCandidateId candidateId,
        DispatchId dispatchId,
        DriverId driverId,
        DispatchCandidateStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new DispatchCandidate(candidateId, dispatchId, driverId, status, createdAt, updatedAt);
    }

    public void updateStatus(DispatchCandidateStatus nextStatus) {
        if (!status.canTransitionTo(nextStatus)) {
            throw new IllegalStateException(
                String.format("후보 상태 %s에서 %s로 전이할 수 없습니다.",
                    status.getDisplayName(), nextStatus.getDisplayName())
            );
        }
        this.status = nextStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
