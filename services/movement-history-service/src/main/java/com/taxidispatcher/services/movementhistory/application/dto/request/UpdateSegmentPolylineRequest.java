package com.taxidispatcher.services.movementhistory.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Segment polyline 갱신 요청 (진행 중 segment 만 허용)")
public class UpdateSegmentPolylineRequest {

    @Schema(description = "Google Encoded Polyline (precision 5)",
            example = "u{~vFvyys@fS]xR}@",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String polyline;
}
