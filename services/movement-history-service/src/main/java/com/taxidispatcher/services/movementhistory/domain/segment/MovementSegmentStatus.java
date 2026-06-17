package com.taxidispatcher.services.movementhistory.domain.segment;

public enum MovementSegmentStatus {
    IN_PROGRESS("진행중"),
    COMPLETED("완료");

    private final String displayName;

    MovementSegmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public boolean canTransitionTo(MovementSegmentStatus nextStatus) {
        return switch (this) {
            case IN_PROGRESS -> nextStatus == COMPLETED;
            case COMPLETED -> false;
        };
    }

    public String getDisplayName() {
        return displayName;
    }
}
