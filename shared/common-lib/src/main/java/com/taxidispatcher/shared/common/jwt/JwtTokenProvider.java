package com.taxidispatcher.shared.common.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Optional;

/**
 * JWT 토큰 생성 및 검증 (RS512)
 * 실제 키는 JwtKeyConfig 빈에서 관리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Optional<PrivateKey> privateKey;
    private final PublicKey publicKey;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    /**
     * 토큰 생성
     *
     * @param accountId     계정 ID (UUID)
     * @param role          권한 (USER | DRIVER)
     * @param actor         도메인별 고유 ID (userId or driverId)
     * @param credentialId  인증 수단 ID
     * @return JWT 토큰
     */
    public String generateToken(String accountId, String role, String actor, String credentialId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(accountId)
                .claim("role", role)
                .claim("actor", actor)
                .claim("credentialId", credentialId)
                .setIssuer("taxi-dispatcher")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(privateKey.orElseThrow(() -> new IllegalStateException("Private key is not available. This service does not issue tokens.")), SignatureAlgorithm.RS512)
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
                    .accountId(claims.getSubject())
                    .role(claims.get("role", String.class))
                    .actor(claims.get("actor", String.class))
                    .credentialId(claims.get("credentialId", String.class))
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
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
