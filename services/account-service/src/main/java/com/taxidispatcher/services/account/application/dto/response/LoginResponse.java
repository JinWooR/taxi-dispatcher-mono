package com.taxidispatcher.services.account.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    @Schema(description = "계정 ID (UUID)", example = "660e8400-e29b-41d4-a716-446655440001")
    private String accountId;

    @Schema(description = "권한 (BASIC / USER / DRIVER 등)", example = "USER")
    private String role;

    @Schema(description = "주체 종류 (ACCOUNT / CUSTOMER / DRIVER)", example = "CUSTOMER")
    private String actor;

    @Schema(description = "인증 수단 ID (UUID)", example = "770e8400-e29b-41d4-a716-446655440002")
    private String credentialId;

    @Schema(description = "발급된 토큰 정보")
    private TokenInfo token;
}
