package com.taxidispatcher.services.movementhistory.application.dto.response;

import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegment;
import com.taxidispatcher.shared.common.util.TimeConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "기사 기간별 이동 이력 응답")
public class DriverPeriodMovementsResponse {

    @Schema(description = "Driver ID")
    private final String driverId;

    @Schema(description = "조회 시작 시각 (UTC ISO 8601)")
    private final Instant startDate;

    @Schema(description = "조회 종료 시각 (UTC ISO 8601)")
    private final Instant endDate;

    @Schema(description = "기간 내 segment 목록 (startedAt 오름차순)")
    private final List<MovementSegmentResponse> segments;

    public static DriverPeriodMovementsResponse of(String driverId,
                                                    LocalDateTime from,
                                                    LocalDateTime to,
                                                    List<MovementSegment> segments) {
        return new DriverPeriodMovementsResponse(
            driverId,
            TimeConverter.toInstant(from),
            TimeConverter.toInstant(to),
            segments.stream().map(MovementSegmentResponse::from).toList()
        );
    }
}
