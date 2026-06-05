package com.taxidispatcher.services.dispatcher.domain.candidate;

public enum DispatchCandidateStatus {
    REQUESTED("요청"),
    ACCEPTED("승인"),
    REJECTED("거절"),
    ACCEPT_CANCELLED("승인 취소"),
    CUSTOMER_CANCELLED("고객 취소"),
    TIMEOUT("시간 초과");

    private final String displayName;

    DispatchCandidateStatus(String displayName) {
        this.displayName = displayName;
    }

    public boolean canTransitionTo(DispatchCandidateStatus nextStatus) {
        return switch (this) {
            case REQUESTED -> nextStatus == ACCEPTED
                || nextStatus == REJECTED
                || nextStatus == ACCEPT_CANCELLED
                || nextStatus == CUSTOMER_CANCELLED
                || nextStatus == TIMEOUT;
            case ACCEPTED -> nextStatus == ACCEPT_CANCELLED
                || nextStatus == CUSTOMER_CANCELLED;
            case REJECTED, ACCEPT_CANCELLED, CUSTOMER_CANCELLED, TIMEOUT -> false;
        };
    }

    public String getDisplayName() {
        return displayName;
    }
}
