package com.taxidispatcher.services.dispatcher.application.dto.request;

import com.taxidispatcher.services.dispatcher.domain.dispatch.DispatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDispatchStatusRequest {

    @Schema(description = "변경할 배차 상태 (도메인 상태 전이 규칙 준수 필요)",
            example = "DEPARTED",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private DispatchStatus status;
}
