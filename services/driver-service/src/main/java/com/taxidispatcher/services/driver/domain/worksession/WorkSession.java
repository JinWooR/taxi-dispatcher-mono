package com.taxidispatcher.services.driver.domain.worksession;

import java.time.LocalDateTime;
import java.util.Objects;

public class WorkSession {
    private WorkSessionId workSessionId;
    private String driverId;
    private WorkSessionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected WorkSession() {
    }

    public WorkSession(WorkSessionId workSessionId, String driverId, LocalDateTime startedAt) {
        this.workSessionId = Objects.requireNonNull(workSessionId);
        this.driverId = Objects.requireNonNull(driverId);
        this.status = WorkSessionStatus.IN_PROGRESS;
        this.startedAt = Objects.requireNonNull(startedAt);
        this.createdAt = startedAt;
        this.updatedAt = startedAt;
    }

    public static WorkSession start(String driverId) {
        return new WorkSession(WorkSessionId.generate(), driverId, LocalDateTime.now());
    }

    public void end() {
        if (this.status == WorkSessionStatus.ENDED) {
            return;
        }
        this.status = WorkSessionStatus.ENDED;
        LocalDateTime now = LocalDateTime.now();
        this.endedAt = now;
        this.updatedAt = now;
    }

    public boolean isInProgress() {
        return this.status == WorkSessionStatus.IN_PROGRESS;
    }

    public WorkSessionId getWorkSessionId() {
        return workSessionId;
    }

    public String getDriverId() {
        return driverId;
    }

    public WorkSessionStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(WorkSessionStatus status) {
        this.status = status;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkSession that = (WorkSession) o;
        return Objects.equals(workSessionId, that.workSessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workSessionId);
    }
}
