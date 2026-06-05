package com.taxidispatcher.services.driver.application.dto.request;

import com.taxidispatcher.services.driver.domain.driver.DriverStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequest {

    @Schema(description = "변경할 기사 상태 (OFFLINE / ONLINE / BUSY)",
            example = "ONLINE",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "상태는 필수입니다")
    private DriverStatus status;
}
