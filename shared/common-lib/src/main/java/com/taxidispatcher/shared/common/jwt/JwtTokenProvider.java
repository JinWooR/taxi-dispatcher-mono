package com.taxidispatcher.shared.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 토큰 생성
     *
     * @param accountId 계정 ID
     * @param type      사용자 타입 (USER | DRIVER)
     * @param email     이메일
     * @return JWT 토큰
     */
    public String generateToken(Long accountId, String type, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(String.valueOf(accountId))
                .claim("type", type)
                .claim("email", email)
                .issuer(jwtProperties.getIssuer())
                .audience().add(jwtProperties.getAudience()).and()
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 토큰에서 사용자 정보 추출
     *
     * @param token JWT 토큰
     * @return AuthUser 인증 사용자 정보
     * @throws JwtException 토큰 검증 실패 시
     */
    public AuthUser validateAndGetUser(String token) {
        try {
            Claims claims = parseToken(token);

            return AuthUser.builder()
                    .accountId(Long.valueOf(claims.getSubject()))
                    .type(claims.get("type", String.class))
                    .email(claims.get("email", String.class))
                    .build();

        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰: {}", e.getMessage());
            throw new JwtException("EXPIRED_TOKEN", "만료된 토큰입니다", e);

        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 형식: {}", e.getMessage());
            throw new JwtException("UNSUPPORTED_TOKEN", "지원하지 않는 토큰 형식입니다", e);

        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 형식: {}", e.getMessage());
            throw new JwtException("MALFORMED_TOKEN", "잘못된 토큰 형식입니다", e);

        } catch (SignatureException e) {
            log.error("JWT 서명 검증 실패: {}", e.getMessage());
            throw new JwtException("INVALID_SIGNATURE", "유효하지 않은 토큰입니다", e);

        } catch (IllegalArgumentException e) {
            log.error("빈 JWT 토큰: {}", e.getMessage());
            throw new JwtException("EMPTY_TOKEN", "토큰이 비어있습니다", e);

        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생: {}", e.getMessage());
            throw new JwtException("TOKEN_ERROR", "토큰 검증 중 오류가 발생했습니다", e);
        }
    }

    /**
     * 토큰 검증 (사용자 정보 반환 없음)
     *
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public boolean isValidToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Claims 파싱
     *
     * @param token JWT 토큰
     * @return Claims
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    /**
     * 서명 키 생성
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
