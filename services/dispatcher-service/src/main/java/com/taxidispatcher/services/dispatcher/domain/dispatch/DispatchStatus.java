package com.taxidispatcher.services.dispatcher.domain.dispatch;

public enum DispatchStatus {
    REQUESTED("요청"),
    CANCELLED("취소"),
    FAILED("실패"),
    ASSIGNED("배차됨"),
    IN_PROGRESS("운행중"),
    ARRIVED("도착"),
    COMPLETED("완료");

    private final String displayName;

    DispatchStatus(String displayName) {
        this.displayName = displayName;
    }

    public boolean canTransitionTo(DispatchStatus nextStatus) {
        return switch (this) {
            case REQUESTED -> nextStatus == ASSIGNED || nextStatus == CANCELLED || nextStatus == FAILED;
            case ASSIGNED -> nextStatus == IN_PROGRESS;
            case IN_PROGRESS -> nextStatus == ARRIVED;
            case ARRIVED -> nextStatus == COMPLETED;
            case CANCELLED, FAILED, COMPLETED -> false;
        };
    }

    public String getDisplayName() {
        return displayName;
    }
}
