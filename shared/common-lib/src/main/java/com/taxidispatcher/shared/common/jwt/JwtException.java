package com.taxidispatcher.shared.common.jwt;

/**
 * JWT 관련 예외
 */
public class JwtException extends RuntimeException {

    private String code;

    public JwtException(String message) {
        super(message);
        this.code = "JWT_ERROR";
    }

    public JwtException(String code, String message) {
        super(message);
        this.code = code;
    }

    public JwtException(String message, Throwable cause) {
        super(message, cause);
        this.code = "JWT_ERROR";
    }

    public JwtException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
