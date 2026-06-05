package com.taxidispatcher.services.customer.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class UpdateCustomerRequest {

    @Schema(description = "변경할 고객 이름", example = "홍길동",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @Schema(description = "변경할 고객 연락처", example = "010-1234-5678",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "전화번호는 필수입니다")
    private String phone;

    protected UpdateCustomerRequest() {
    }

    public UpdateCustomerRequest(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
