package com.taxidispatcher.services.account.application.service;

import com.taxidispatcher.services.account.application.dto.response.LoginResponse;
import com.taxidispatcher.services.account.application.dto.response.TokenInfo;
import com.taxidispatcher.services.account.domain.account.Account;
import com.taxidispatcher.services.account.domain.account.AccountId;
import com.taxidispatcher.services.account.domain.token.RefreshToken;
import com.taxidispatcher.services.account.domain.token.RefreshTokenRepository;
import com.taxidispatcher.services.account.domain.token.TokenId;
import com.taxidispatcher.shared.common.exception.DomainException;
import com.taxidispatcher.shared.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountService accountService;

    @Value("${jwt.expiration.access}")
    private long accessExpiration;

    @Value("${jwt.expiration.refresh}")
    private long refreshExpiration;

    @Transactional
    public LoginResponse login(Account account, String role, String actor, String credentialId) {
        String accountId = account.getAccountId().getValue();

        String accessToken = jwtTokenProvider.generateAccessToken(accountId, role, actor, credentialId);

        refreshTokenRepository.deleteAllByAccountId(account.getAccountId());

        TokenId tokenId = TokenId.generate();
        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(tokenId.getValue());

        RefreshToken refreshToken = new RefreshToken(
                tokenId,
                account.getAccountId(),
                hashToken(refreshTokenStr),
                role,
                actor,
                LocalDateTime.now().plusSeconds(refreshExpiration / 1000)
        );
        refreshTokenRepository.save(refreshToken);

        LocalDateTime now = LocalDateTime.now();
        TokenInfo tokenInfo = TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .accessExpiresAt(now.plusSeconds(accessExpiration / 1000))
                .refreshExpiresAt(now.plusSeconds(refreshExpiration / 1000))
                .build();

        return LoginResponse.builder()
                .accountId(accountId)
                .role(role)
                .actor(actor)
                .credentialId(credentialId)
                .token(tokenInfo)
                .build();
    }

    @Transactional
    public TokenInfo refreshAccessToken(String refreshTokenStr) {
        String tokenId = jwtTokenProvider.extractTokenId(refreshTokenStr);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenId(TokenId.of(tokenId))
                .orElseThrow(() -> new DomainException("TOKEN_NOT_FOUND", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.deleteByTokenId(TokenId.of(tokenId));
            throw new DomainException("TOKEN_EXPIRED", "만료된 토큰입니다", HttpStatus.UNAUTHORIZED);
        }

        if (!refreshToken.getTokenHash().equals(hashToken(refreshTokenStr))) {
            throw new DomainException("TOKEN_INVALID", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED);
        }

        Account account = accountService.getAccount(refreshToken.getAccountId());
        String credentialId = account.getCredentials().stream()
                .filter(c -> c.getType().equals("BASIC"))
                .map(c -> c.getCredentialId().getValue())
                .findFirst().orElse(null);

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                account.getAccountId().getValue(),
                refreshToken.getRole(),
                refreshToken.getActor(),
                credentialId);

        return TokenInfo.builder()
                .accessToken(newAccessToken)
                .accessExpiresAt(LocalDateTime.now().plusSeconds(accessExpiration / 1000))
                .build();
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        String tokenId = jwtTokenProvider.extractTokenId(refreshTokenStr);
        refreshTokenRepository.deleteByTokenId(TokenId.of(tokenId));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("토큰 해시 생성 실패", e);
        }
    }
}
