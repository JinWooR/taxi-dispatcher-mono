package com.taxidispatcher.services.movementhistory.application.dto.response;

import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegment;
import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegmentStatus;
import com.taxidispatcher.shared.common.util.TimeConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Schema(description = "이동 segment 단건 응답")
public class MovementSegmentResponse {

    @Schema(description = "Segment ID (auto increment)")
    private final Long id;

    @Schema(description = "Work Session ID")
    private final String workSessionId;

    @Schema(description = "Driver ID")
    private final String driverId;

    @Schema(description = "Dispatch ID (배차 운행 중 segment 만 존재)")
    private final String dispatchId;

    @Schema(description = "Segment 번호")
    private final int segmentNo;

    @Schema(description = "Encoded polyline (precision 5)")
    private final String polyline;

    @Schema(description = "상태 (IN_PROGRESS | COMPLETED)")
    private final MovementSegmentStatus status;

    @Schema(description = "시작 시각 (UTC ISO 8601)")
    private final Instant startedAt;

    @Schema(description = "종료 시각 (UTC ISO 8601, IN_PROGRESS 일 때 null)")
    private final Instant endedAt;

    @Schema(description = "갱신 시각 (UTC ISO 8601)")
    private final Instant updatedAt;

    public static MovementSegmentResponse from(MovementSegment segment) {
        return new MovementSegmentResponse(
            segment.getId(),
            segment.getWorkSessionId().getValue(),
            segment.getDriverId().getValue(),
            segment.getDispatchId() != null ? segment.getDispatchId().getValue() : null,
            segment.getSegmentNo(),
            segment.getPolyline().getValue(),
            segment.getStatus(),
            TimeConverter.toInstant(segment.getStartedAt()),
            TimeConverter.toInstant(segment.getEndedAt()),
            TimeConverter.toInstant(segment.getUpdatedAt())
        );
    }
}
