package com.taxidispatcher.services.driver.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverRequest {

    @Schema(description = "변경할 기사 이름", example = "김기사",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    private String name;

    @Schema(description = "변경할 기사 연락처", example = "010-9876-5432",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 20)
    @NotBlank(message = "전화번호는 필수입니다")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phoneNumber;

    @Schema(description = "변경할 운전면허 번호", example = "12-34-567890-12",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "면허번호는 필수입니다")
    @Size(max = 50, message = "면허번호는 50자 이하여야 합니다")
    private String licenseNumber;

    @Schema(description = "변경할 차량 번호판", example = "12가3456",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 20)
    @NotBlank(message = "차량번호는 필수입니다")
    @Size(max = 20, message = "차량번호는 20자 이하여야 합니다")
    private String plateNumber;

    @Schema(description = "변경할 차종 (SEDAN / SUV / VAN 등)", example = "SEDAN",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "차종은 필수입니다")
    @Size(max = 50, message = "차종은 50자 이하여야 합니다")
    private String vehicleType;
}
