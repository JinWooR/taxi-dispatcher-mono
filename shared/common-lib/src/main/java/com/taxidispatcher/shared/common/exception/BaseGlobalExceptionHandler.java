package com.taxidispatcher.shared.common.exception;

import com.taxidispatcher.shared.common.jwt.JwtException;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 글로벌 예외 처리 기본 클래스
 * 공통 예외(검증 실패, JWT, 기본 예외)를 처리하고,
 * 각 서비스는 이를 상속받아 서비스별 예외를 추가 처리합니다.
 *
 * 상속 시 @RestControllerAdvice를 구현체에 붙입니다.
 */
@Slf4j
public abstract class BaseGlobalExceptionHandler {

    /**
     * Spring Validation 검증 실패 처리
     * @Valid 어노테이션이 붙은 파라미터 검증 실패 시 호출
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        List<CommonResponse.FieldError> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new CommonResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        CommonResponse<?> response = CommonResponse.validationError("입력 값 검증 실패", errors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 도메인 예외 처리
     * 모든 마이크로서비스의 도메인 로직에서 발생하는 예외
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<CommonResponse<?>> handleDomainException(
            DomainException e,
            HttpServletRequest request) {

        log.warn("DomainException 발생: code={}, message={}", e.getCode(), e.getMessage());

        CommonResponse<?> response = CommonResponse.error(
                e.getCode(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    /**
     * JWT 관련 예외 처리
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<CommonResponse<?>> handleJwtException(
            JwtException e,
            HttpServletRequest request) {

        log.warn("JWT 예외 발생: code={}, message={}", e.getCode(), e.getMessage());

        CommonResponse<?> response = CommonResponse.error(
                e.getCode(),
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 접근 권한 부족 처리
     * @PreAuthorize 검증 실패 시 발생하는 AuthorizationDeniedException 포함
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<?>> handleAccessDeniedException(
            AccessDeniedException e,
            HttpServletRequest request) {

        log.warn("접근 권한 부족: message={}", e.getMessage());

        CommonResponse<?> response = CommonResponse.error(
                "FORBIDDEN",
                "접근 권한이 없습니다",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * RuntimeException 기본 처리
     * 예상하지 못한 런타임 예외 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<?>> handleRuntimeException(
            RuntimeException e,
            HttpServletRequest request) {

        log.error("RuntimeException 발생: message={}", e.getMessage(), e);

        CommonResponse<?> response = CommonResponse.error(
                "INTERNAL_SERVER_ERROR",
                "서버 오류가 발생했습니다",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Exception 최종 처리 (fallback)
     * 모든 체크 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(
            Exception e,
            HttpServletRequest request) {

        log.error("Exception 발생: message={}", e.getMessage(), e);

        CommonResponse<?> response = CommonResponse.error(
                "INTERNAL_SERVER_ERROR",
                "서버 오류가 발생했습니다",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
