package com.taxidispatcher.services.user.presentation;

import com.taxidispatcher.shared.common.exception.BaseGlobalExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 사용자 서비스 글로벌 예외 처리
 * BaseGlobalExceptionHandler를 상속받아 공통 예외 처리를 재사용합니다.
 * DomainException의 httpStatus 필드를 통해 도메인 예외의 HTTP 상태가 자동 결정됩니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {
    // BaseGlobalExceptionHandler의 공통 처리로 충분함
}
