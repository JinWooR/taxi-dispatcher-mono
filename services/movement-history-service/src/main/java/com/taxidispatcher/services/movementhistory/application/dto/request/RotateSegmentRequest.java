package com.taxidispatcher.services.movementhistory.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Segment 전환 요청. current = 종료될 segment 의 최종 polyline (활성 있을 때만), next = 새 segment 시작값")
public record RotateSegmentRequest(

    @Valid
    @Schema(description = "종료될 segment 갱신 정보. 활성 segment 가 없으면 null/생략. " +
            "current 있으면 안의 polyline 은 @NotBlank 필수. 마지막 PUT 이후 누락 좌표 보강용.")
    UpdateSegmentPolylineRequest current,

    @Valid
    @NotNull
    @Schema(description = "새 segment 시작 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    StartWorkSessionSegmentRequest next

) {}
