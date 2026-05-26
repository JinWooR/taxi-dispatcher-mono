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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Optional<PrivateKey> privateKey;
    private final PublicKey publicKey;

    @Value("${jwt.expiration.access:0}")
    private long accessExpiration;

    @Value("${jwt.expiration.refresh:0}")
    private long refreshExpiration;

    public String generateAccessToken(String accountId, String role, String actor, String credentialId) {
        if (accessExpiration == 0) {
            throw new IllegalStateException("jwt.expiration.access is not configured.");
        }
        Date now = new Date();

        return Jwts.builder()
                .setSubject(accountId)
                .claim("type", "ACCESS")
                .claim("role", role)
                .claim("actor", actor)
                .claim("credentialId", credentialId)
                .setIssuer("taxi-dispatcher")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExpiration))
                .signWith(resolvePrivateKey(), SignatureAlgorithm.RS512)
                .compact();
    }

    public String generateRefreshToken(String tokenId) {
        if (refreshExpiration == 0) {
            throw new IllegalStateException("jwt.expiration.refresh is not configured.");
        }
        Date now = new Date();

        return Jwts.builder()
                .claim("type", "REFRESH")
                .claim("tokenId", tokenId)
                .setIssuer("taxi-dispatcher")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpiration))
                .signWith(resolvePrivateKey(), SignatureAlgorithm.RS512)
                .compact();
    }

    public AuthUser validateAndGetUser(String token) {
        try {
            Claims claims = parseToken(token);

            return AuthUser.builder()
                    .accountId(claims.getSubject())
                    .type(claims.get("type", String.class))
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

    public AuthUser validateAndGetAccessUser(String token) {
        AuthUser authUser = validateAndGetUser(token);
        if (!"ACCESS".equals(authUser.getType())) {
            throw new JwtException("INVALID_TOKEN_TYPE", "Access Token이 아닙니다");
        }
        return authUser;
    }

    public String extractTokenId(String token) {
        return parseToken(token).get("tokenId", String.class);
    }

    public boolean isValidToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private PrivateKey resolvePrivateKey() {
        return privateKey.orElseThrow(() ->
                new IllegalStateException("Private key is not available. This service does not issue tokens."));
    }
}
