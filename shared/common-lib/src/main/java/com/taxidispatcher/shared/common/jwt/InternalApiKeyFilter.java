package com.taxidispatcher.shared.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 내부 API 호출 시 API Key 검증 필터
 * Authorization: ApiKey {key} 형식
 * /internal/** 경로 전용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String AUTH_SCHEME = "ApiKey ";

    private final ObjectMapper objectMapper;

    @Value("${internal.api-key:}")
    private String myApiKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!StringUtils.hasText(myApiKey)) {
            log.error("internal.api-key 환경변수가 설정되지 않음");
            writeUnauthorized(request, response, "내부 API Key가 설정되지 않았습니다");
            return;
        }

        String providedKey = extractApiKey(request);
        if (providedKey == null) {
            writeUnauthorized(request, response, "Authorization 헤더가 없거나 형식이 잘못되었습니다");
            return;
        }

        if (!myApiKey.equals(providedKey)) {
            log.warn("내부 API Key 불일치 - uri: {}", request.getRequestURI());
            writeUnauthorized(request, response, "유효하지 않은 API Key입니다");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractApiKey(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith(AUTH_SCHEME)) {
            return null;
        }
        return header.substring(AUTH_SCHEME.length());
    }

    private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        CommonResponse<Object> errorResponse = CommonResponse.error(
                "INTERNAL_UNAUTHORIZED",
                message,
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
