package com.taxidispatcher.services.driver.application.dto.response;

import com.taxidispatcher.services.driver.domain.worksession.WorkSession;
import com.taxidispatcher.services.driver.domain.worksession.WorkSessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record WorkSessionResponse(

        @Schema(description = "근무 세션 ID (UUID)", example = "990e8400-e29b-41d4-a716-446655440004")
        String workSessionId,

        @Schema(description = "기사 ID (UUID)", example = "770e8400-e29b-41d4-a716-446655440002")
        String driverId,

        @Schema(description = "근무 세션 상태", example = "IN_PROGRESS")
        WorkSessionStatus status,

        @Schema(description = "근무 시작 시각 (UTC, ISO 8601)", example = "2026-06-08T05:30:00Z")
        LocalDateTime startedAt,

        @Schema(description = "근무 종료 시각 (UTC, ISO 8601). IN_PROGRESS인 경우 null", example = "2026-06-08T13:45:00Z")
        LocalDateTime endedAt
) {
    public static WorkSessionResponse from(WorkSession workSession) {
        return new WorkSessionResponse(
                workSession.getWorkSessionId().getValue(),
                workSession.getDriverId(),
                workSession.getStatus(),
                workSession.getStartedAt(),
                workSession.getEndedAt()
        );
    }
}
