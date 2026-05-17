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
     * Account ID (토큰의 sub claim)
     */
    private Long accountId;

    /**
     * 사용자 타입 (USER | DRIVER)
     */
    private String type;

    /**
     * 이메일
     */
    private String email;

    /**
     * 사용자 타입이 USER인지 확인
     */
    public boolean isUser() {
        return "USER".equals(this.type);
    }

    /**
     * 사용자 타입이 DRIVER인지 확인
     */
    public boolean isDriver() {
        return "DRIVER".equals(this.type);
    }
}
