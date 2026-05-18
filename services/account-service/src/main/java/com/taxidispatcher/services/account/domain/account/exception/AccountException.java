package com.taxidispatcher.services.account.domain.account.exception;

/**
 * 계정 관련 기본 예외
 */
public class AccountException extends RuntimeException {

    private String code;

    public AccountException(String message) {
        super(message);
        this.code = "ACCOUNT_ERROR";
    }

    public AccountException(String code, String message) {
        super(message);
        this.code = code;
    }

    public AccountException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
