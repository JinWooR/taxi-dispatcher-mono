package com.taxidispatcher.services.movementhistory.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "근무 세션 segment 시작 요청")
public class StartWorkSessionSegmentRequest {

    @Schema(description = "Segment 번호 (1부터 시작, 동일 근무 세션 내 유일)", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(1)
    private Integer segmentNo;

    @Schema(description = "Google Encoded Polyline (precision 5)", example = "u{~vFvyys@fS]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String polyline;

    @Schema(description = "배차 ID (배차 운행 중 segment 인 경우만 전달)",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private String dispatchId;
}
