package com.taxidispatcher.services.account.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @Schema(description = "로그인 ID (이메일)", example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "로그인 ID는 필수입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String loginId;

    @Schema(description = "비밀번호", example = "P@ssw0rd!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
