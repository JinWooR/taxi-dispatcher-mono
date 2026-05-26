package com.taxidispatcher.shared.common.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * JWT 인증 필터
 * Authorization 헤더에서 토큰을 추출하고 검증
 * Spring Security Context에 인증 정보 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.isValidToken(token)) {
                // 토큰 검증 완료
                AuthUser authUser = jwtTokenProvider.validateAndGetUser(token);

                // Spring Security Context에 인증 정보 설정 (권한: ROLE_NONE / ROLE_USER / ROLE_DRIVER)
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        authUser,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + authUser.getRole()))
                    );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT 토큰 검증 성공 - accountId: {}, role: {}, credentialId: {}",
                    authUser.getAccountId(), authUser.getRole(), authUser.getCredentialId());
            }

        } catch (JwtException e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();

        } catch (Exception e) {
            log.error("필터 처리 중 오류: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 토큰 추출
     *
     * @param request HttpServletRequest
     * @return JWT 토큰 (Bearer 제거)
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 제거
        }

        return null;
    }
}
