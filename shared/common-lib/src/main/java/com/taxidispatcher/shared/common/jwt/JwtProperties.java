package com.taxidispatcher.shared.common.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 Properties
 * application.yml에서 jwt.* 설정 바인딩
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명 비밀키
     */
    private String secret;

    /**
     * 토큰 만료 시간 (밀리초)
     */
    private long expiration = 3600000L;  // 기본값: 1시간

    /**
     * Refresh 토큰 만료 시간 (밀리초)
     */
    private long refreshExpiration = 86400000L;  // 기본값: 24시간

    /**
     * 토큰 발급자 (issuer)
     */
    private String issuer = "taxi-dispatcher";

    /**
     * 토큰 대상 (audience)
     */
    private String audience = "taxi-dispatcher-users";
}
