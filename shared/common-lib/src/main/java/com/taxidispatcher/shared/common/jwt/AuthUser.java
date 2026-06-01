package com.taxidispatcher.shared.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 인증된 사용자 정보
 * JWT 토큰에서 추출한 최소한의 정보만 포함
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {

    /**
     * Account ID (토큰의 sub claim, UUID)
     */
    private String accountId;

    /**
     * 토큰 타입 (ACCESS | REFRESH)
     */
    private String type;

    /**
     * 권한 (CUSTOMER | DRIVER | NONE)
     */
    private String role;

    /**
     * 도메인별 고유 ID (customerId or driverId)
     */
    private String actor;

    /**
     * 인증 수단 ID (credentialId)
     */
    private String credentialId;

    /**
     * 권한이 CUSTOMER인지 확인
     */
    public boolean isCustomer() {
        return "CUSTOMER".equals(this.role);
    }

    /**
     * 권한이 DRIVER인지 확인
     */
    public boolean isDriver() {
        return "DRIVER".equals(this.role);
    }
}
