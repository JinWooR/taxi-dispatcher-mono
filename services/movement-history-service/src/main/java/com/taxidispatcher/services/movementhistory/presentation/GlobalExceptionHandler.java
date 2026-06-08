package com.taxidispatcher.services.movementhistory.presentation;

import com.taxidispatcher.shared.common.exception.BaseGlobalExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Movement History Service 글로벌 예외 처리
 * BaseGlobalExceptionHandler 의 공통 처리(DomainException, JwtException 등) 로 충분.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {
}
