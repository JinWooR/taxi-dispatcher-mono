package com.taxidispatcher.services.driver.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationRequest {

    @Schema(description = "현재 위치 - 위도 (-90 ~ 90)", example = "37.5665",
            requiredMode = Schema.RequiredMode.REQUIRED, minimum = "-90", maximum = "90")
    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다")
    private Double latitude;

    @Schema(description = "현재 위치 - 경도 (-180 ~ 180)", example = "126.9780",
            requiredMode = Schema.RequiredMode.REQUIRED, minimum = "-180", maximum = "180")
    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다")
    private Double longitude;
}
