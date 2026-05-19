package com.taxidispatcher.services.driver.application.dto.request;

import com.taxidispatcher.services.driver.domain.driver.DriverStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequest {

    @NotNull(message = "상태는 필수입니다")
    private DriverStatus status;
}
