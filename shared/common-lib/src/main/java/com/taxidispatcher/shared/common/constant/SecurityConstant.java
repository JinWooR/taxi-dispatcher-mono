package com.taxidispatcher.shared.common.constant;

/**
 * 보안 관련 상수 정의
 */
public class SecurityConstant {

    // JWT 관련
    public static final String JWT_SECRET_KEY = "jwt.secret.key";
    public static final String JWT_EXPIRATION_MS = "jwt.expiration.ms";
    public static final String JWT_REFRESH_EXPIRATION_MS = "jwt.refresh.expiration.ms";

    // 토큰 타입
    public static final String TOKEN_TYPE_BEARER = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // 사용자 타입
    public static final String USER_TYPE_USER = "USER";
    public static final String USER_TYPE_DRIVER = "DRIVER";

    // 기본값 (환경변수 없을 경우)
    public static final long DEFAULT_EXPIRATION_MS = 3600000L;  // 1시간
    public static final long DEFAULT_REFRESH_EXPIRATION_MS = 86400000L;  // 24시간

    // 에러 메시지
    public static final String INVALID_TOKEN_MESSAGE = "유효하지 않은 토큰입니다";
    public static final String EXPIRED_TOKEN_MESSAGE = "만료된 토큰입니다";
    public static final String MISSING_TOKEN_MESSAGE = "토큰이 없습니다";

    private SecurityConstant() {
        throw new IllegalStateException("Utility class");
    }
}
