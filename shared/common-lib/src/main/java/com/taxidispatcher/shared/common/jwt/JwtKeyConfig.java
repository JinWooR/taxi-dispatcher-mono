package com.taxidispatcher.shared.common.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * JWT 암호화 키 설정
 * PEM 형식 RSA 공개/비밀키를 환경변수에서 로드하여 빈으로 등록
 */
@Slf4j
@Configuration
public class JwtKeyConfig {

    /**
     * RSA 비밀키 빈 등록
     * 토큰 발급 시 사용
     * JWT_PRIVATE_KEY가 설정되어 있을 때만 생성 (account-service만)
     */
    @Bean
    @ConditionalOnProperty(name = "jwt.privateKey")
    public PrivateKey jwtPrivateKey(@Value("${jwt.privateKey}") String privateKeyPem) {
        try {
            if (privateKeyPem == null || privateKeyPem.isEmpty()) {
                throw new IllegalArgumentException("privateKey is null or empty. Check JWT_PRIVATE_KEY environment variable");
            }

            byte[] decodedKey = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey key = kf.generatePrivate(spec);

            log.info("RSA 비밀키 로드 완료");
            return key;
        } catch (Exception e) {
            log.error("비밀키 로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load private key: " + e.getMessage(), e);
        }
    }

    /**
     * RSA 공개키 빈 등록
     * 토큰 검증 시 사용
     */
    @Bean
    public PublicKey jwtPublicKey(@Value("${jwt.publicKey}") String publicKeyPem) {
        try {
            if (publicKeyPem == null || publicKeyPem.isEmpty()) {
                throw new IllegalArgumentException("publicKey is null or empty. Check JWT_PUBLIC_KEY environment variable");
            }

            byte[] decodedKey = Base64.getDecoder().decode(publicKeyPem);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey key = kf.generatePublic(spec);

            log.info("RSA 공개키 로드 완료");
            return key;
        } catch (Exception e) {
            log.error("공개키 로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load public key: " + e.getMessage(), e);
        }
    }
}
