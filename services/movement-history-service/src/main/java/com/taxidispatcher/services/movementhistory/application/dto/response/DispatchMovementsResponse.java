package com.taxidispatcher.services.movementhistory.application.dto.response;

import com.taxidispatcher.services.movementhistory.domain.segment.MovementSegment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "배차 단위 이동 이력 응답 (segment 목록)")
public class DispatchMovementsResponse {

    @Schema(description = "Dispatch ID")
    private final String dispatchId;

    @Schema(description = "Segment 목록 (segmentNo 오름차순)")
    private final List<MovementSegmentResponse> segments;

    public static DispatchMovementsResponse of(String dispatchId, List<MovementSegment> segments) {
        return new DispatchMovementsResponse(
            dispatchId,
            segments.stream().map(MovementSegmentResponse::from).toList()
        );
    }
}
