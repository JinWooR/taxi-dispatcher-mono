package com.taxidispatcher.services.account.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /**
     * JWT 토큰
     */
    private String token;

    /**
     * 토큰 만료 시간 (초)
     */
    private Long expiresIn;

    /**
     * Account ID (UUID)
     */
    private String accountId;

    /**
     * 권한 (USER | DRIVER)
     */
    private String role;

    /**
     * 도메인별 고유 ID (userId or driverId)
     */
    private String actor;

    /**
     * 인증 수단 ID (credentialId)
     */
    private String credentialId;
}
