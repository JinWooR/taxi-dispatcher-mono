package com.taxidispatcher.services.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserRequest {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다")
    private String phone;

    protected UpdateUserRequest() {
    }

    public UpdateUserRequest(String name, String phone) {
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
