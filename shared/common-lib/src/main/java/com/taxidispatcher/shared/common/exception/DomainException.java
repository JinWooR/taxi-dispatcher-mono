package com.taxidispatcher.shared.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 도메인 예외
 * 모든 마이크로서비스에서 공통으로 사용하는 도메인 예외입니다.
 * 각 서비스별로 예외 클래스를 따로 만들 필요 없이 이 클래스를 직접 사용합니다.
 *
 * 에러 코드 정책:
 * - 형식: [SERVICE_PREFIX]_[상태] (예: ACCOUNT_NOT_FOUND, USER_INVALID_PHONE)
 * - docs/05-api-common-rules.md의 에러 코드는 가이드라인이며, 각 서비스가 자신의 도메인 로직에 맞게 정의 가능
 * - HTTP 상태는 기본 400(Bad Request)으로 설정되며, 필요시 명시적으로 지정 가능
 *
 * 사용 예시:
 * - throw new DomainException("ACCOUNT_NOT_FOUND", "계정을 찾을 수 없습니다");
 * - throw new DomainException("ACCOUNT_NOT_FOUND", "계정을 찾을 수 없습니다", HttpStatus.NOT_FOUND);
 * - throw new DomainException("USER_INVALID_PHONE", "유효하지 않은 전화번호", HttpStatus.BAD_REQUEST);
 * - throw new DomainException("DRIVER_OFFLINE", "기사가 오프라인 상태", HttpStatus.SERVICE_UNAVAILABLE);
 */
public class DomainException extends RuntimeException {

    private String code;
    private HttpStatus httpStatus;

    /**
     * 기본 HTTP 상태(400)로 도메인 예외 생성
     */
    public DomainException(String code, String message) {
        this(code, message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 지정된 HTTP 상태로 도메인 예외 생성
     */
    public DomainException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    /**
     * Cause와 함께 기본 HTTP 상태(400)로 도메인 예외 생성
     */
    public DomainException(String code, String message, Throwable cause) {
        this(code, message, HttpStatus.BAD_REQUEST, cause);
    }

    /**
     * Cause와 함께 지정된 HTTP 상태로 도메인 예외 생성
     */
    public DomainException(String code, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
