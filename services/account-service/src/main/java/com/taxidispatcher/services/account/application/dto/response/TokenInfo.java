package com.taxidispatcher.services.account.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInfo {

    @Schema(description = "Access Token (JWT). Authorization 헤더 Bearer 스킴 사용")
    private String accessToken;

    @Schema(description = "Refresh Token (JWT). 액세스 토큰 재발급/로그아웃 시 사용")
    private String refreshToken;

    @Schema(description = "Access Token 만료 시각 (UTC, ISO 8601)", example = "2026-06-04T06:30:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime accessExpiresAt;

    @Schema(description = "Refresh Token 만료 시각 (UTC, ISO 8601)", example = "2026-06-11T05:30:00Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime refreshExpiresAt;

    @Schema(description = "토큰 권한", example = "USER")
    private String role;

    @Schema(description = "주체 종류", example = "CUSTOMER")
    private String actor;
}
