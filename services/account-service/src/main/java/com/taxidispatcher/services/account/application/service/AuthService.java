package com.taxidispatcher.services.account.application.service;

import com.taxidispatcher.services.account.application.dto.response.LoginResponse;
import com.taxidispatcher.services.account.application.dto.response.TokenInfo;
import com.taxidispatcher.services.account.domain.account.Account;
import com.taxidispatcher.services.account.domain.account.AccountId;
import com.taxidispatcher.services.account.domain.token.RefreshToken;
import com.taxidispatcher.services.account.domain.token.RefreshTokenRepository;
import com.taxidispatcher.services.account.domain.token.TokenId;
import com.taxidispatcher.services.account.infrastructure.client.DriverServiceClient;
import com.taxidispatcher.services.account.infrastructure.client.UserServiceClient;
import com.taxidispatcher.shared.common.dto.driver.internal.DriverInternalProfile;
import com.taxidispatcher.shared.common.dto.user.internal.UserInternalProfile;
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
    private final UserServiceClient userServiceClient;
    private final DriverServiceClient driverServiceClient;

    @Value("${jwt.expiration.access}")
    private long accessExpiration;

    @Value("${jwt.expiration.refresh}")
    private long refreshExpiration;

    /**
     * 역할 기반 로그인 - 프로필 존재 확인 후 actor 자동 조회
     */
    @Transactional
    public LoginResponse loginAsRole(Account account, String role, String credentialId) {
        String actor = lookupActorByRole(account.getAccountId().getValue(), role);
        return login(account, role, actor, credentialId);
    }

    /**
     * 역할별 actor(프로필 ID) 조회
     * USER → user-service에서 userId 조회
     * DRIVER → driver-service에서 driverId 조회
     */
    private String lookupActorByRole(String accountId, String role) {
        if ("USER".equals(role)) {
            UserInternalProfile userProfile = userServiceClient.findByAccountId(accountId)
                    .orElseThrow(() -> new DomainException(
                            "USER_PROFILE_NOT_FOUND",
                            "사용자 프로필이 존재하지 않습니다. 프로필 등록 후 시도하세요.",
                            HttpStatus.NOT_FOUND));
            return userProfile.getUserId();
        }
        if ("DRIVER".equals(role)) {
            DriverInternalProfile driverProfile = driverServiceClient.findByAccountId(accountId)
                    .orElseThrow(() -> new DomainException(
                            "DRIVER_PROFILE_NOT_FOUND",
                            "기사 프로필이 존재하지 않습니다. 프로필 등록 후 시도하세요.",
                            HttpStatus.NOT_FOUND));
            return driverProfile.getDriverId();
        }
        return null;
    }

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
                .role(role)
                .actor(actor)
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
                .role(refreshToken.getRole())
                .actor(refreshToken.getActor())
                .build();
    }

    /**
     * 권한 승격 (Refresh Token 기반)
     * Refresh Token은 그대로 유지, DB의 role/actor만 업데이트, 새 Access Token만 발급
     * 프로필 존재 확인 및 actor 조회는 lookupActorByRole에서 처리
     * TODO: 잘못된 경로 호출 검증 (예: USER가 driver 승격 시도)
     */
    @Transactional
    public LoginResponse upgradeRole(String refreshTokenStr, String newRole) {
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

        // newRole에 맞는 프로필 존재 확인 및 actor 조회 (없으면 예외 발생)
        String newActor = lookupActorByRole(account.getAccountId().getValue(), newRole);

        // RefreshToken DB의 role/actor 업데이트 (토큰 자체는 유지)
        refreshToken.updateRole(newRole, newActor);
        refreshTokenRepository.save(refreshToken);

        // 새 Access Token만 발급
        String accountId = account.getAccountId().getValue();
        String newAccessToken = jwtTokenProvider.generateAccessToken(accountId, newRole, newActor, credentialId);

        TokenInfo tokenInfo = TokenInfo.builder()
                .accessToken(newAccessToken)
                .accessExpiresAt(LocalDateTime.now().plusSeconds(accessExpiration / 1000))
                .role(newRole)
                .actor(newActor)
                .build();

        return LoginResponse.builder()
                .accountId(accountId)
                .role(newRole)
                .actor(newActor)
                .credentialId(credentialId)
                .token(tokenInfo)
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
