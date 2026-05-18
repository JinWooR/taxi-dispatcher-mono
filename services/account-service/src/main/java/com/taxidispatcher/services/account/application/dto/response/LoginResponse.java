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
     * Account ID
     */
    private String accountId;

    /**
     * 로그인 ID (이메일)
     */
    private String loginId;
}
