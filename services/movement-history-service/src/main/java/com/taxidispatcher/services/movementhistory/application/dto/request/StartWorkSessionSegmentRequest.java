package com.taxidispatcher.services.movementhistory.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "근무 세션 segment 시작 요청 (segmentNo 는 서버 자동 할당)")
public class StartWorkSessionSegmentRequest {

    @Schema(description = "Google Encoded Polyline (precision 5)", example = "u{~vFvyys@fS]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String polyline;

    @Schema(description = "배차 ID (배차 운행 중 segment 인 경우만 전달)",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private String dispatchId;
}
