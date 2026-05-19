package com.taxidispatcher.services.driver.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDriverRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phoneNumber;

    @NotBlank(message = "면허번호는 필수입니다")
    @Size(max = 50, message = "면허번호는 50자 이하여야 합니다")
    private String licenseNumber;

    @NotBlank(message = "차량번호는 필수입니다")
    @Size(max = 20, message = "차량번호는 20자 이하여야 합니다")
    private String plateNumber;

    @NotBlank(message = "차종은 필수입니다")
    @Size(max = 50, message = "차종은 50자 이하여야 합니다")
    private String vehicleType;
}
