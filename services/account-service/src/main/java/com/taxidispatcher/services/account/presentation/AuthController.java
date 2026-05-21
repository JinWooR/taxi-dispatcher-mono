package com.taxidispatcher.services.account.presentation;

import com.taxidispatcher.services.account.application.dto.request.LoginRequest;
import com.taxidispatcher.services.account.application.dto.request.RegisterRequest;
import com.taxidispatcher.services.account.application.dto.response.LoginResponse;
import com.taxidispatcher.services.account.application.dto.response.RegisterResponse;
import com.taxidispatcher.services.account.application.service.AccountService;
import com.taxidispatcher.services.account.domain.account.Account;
import com.taxidispatcher.shared.common.jwt.JwtTokenProvider;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Override
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        Account account = accountService.registerAccount(request.getLoginId(), request.getPassword());
        return ResponseEntity.ok(CommonResponse.success(RegisterResponse.from(account), "회원가입 완료"));
    }

    /**
     * 기본 로그인 (프로필 등록 전)
     */
    @Override
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Account account = accountService.loginAccount(request.getLoginId(), request.getPassword());

        String credentialId = account.findBasicCredential(request.getLoginId())
                .map(cred -> cred.getCredentialId().getValue())
                .orElse(null);

        String token = jwtTokenProvider.generateToken(
                account.getAccountId().getValue(),
                "NONE",
                null,
                credentialId
        );

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .expiresIn(3600L)
                .accountId(account.getAccountId().getValue())
                .role("NONE")
                .actor(null)
                .credentialId(credentialId)
                .build();

        return ResponseEntity.ok(CommonResponse.success(response, "로그인 성공"));
    }

    /**
     * 사용자 권한으로 로그인
     * TODO: user-service 내부 API 통합 후 userId 조회 구현
     */
    @PostMapping("/login/user")
    public ResponseEntity<CommonResponse<LoginResponse>> loginUser(@Valid @RequestBody LoginRequest request) {
        Account account = accountService.loginAccount(request.getLoginId(), request.getPassword());

        String credentialId = account.findBasicCredential(request.getLoginId())
                .map(cred -> cred.getCredentialId().getValue())
                .orElse(null);

        // TODO: user-service API를 통해 userId 조회 후 actor에 설정
        String userId = null;

        String token = jwtTokenProvider.generateToken(
                account.getAccountId().getValue(),
                "USER",
                userId,
                credentialId
        );

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .expiresIn(3600L)
                .accountId(account.getAccountId().getValue())
                .role("USER")
                .actor(userId)
                .credentialId(credentialId)
                .build();

        return ResponseEntity.ok(CommonResponse.success(response, "사용자 로그인 성공"));
    }

    /**
     * 기사 권한으로 로그인
     * TODO: driver-service 내부 API 통합 후 driverId 조회 구현
     */
    @PostMapping("/login/driver")
    public ResponseEntity<CommonResponse<LoginResponse>> loginDriver(@Valid @RequestBody LoginRequest request) {
        Account account = accountService.loginAccount(request.getLoginId(), request.getPassword());

        String credentialId = account.findBasicCredential(request.getLoginId())
                .map(cred -> cred.getCredentialId().getValue())
                .orElse(null);

        // TODO: driver-service API를 통해 driverId 조회 후 actor에 설정
        String driverId = null;

        String token = jwtTokenProvider.generateToken(
                account.getAccountId().getValue(),
                "DRIVER",
                driverId,
                credentialId
        );

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .expiresIn(3600L)
                .accountId(account.getAccountId().getValue())
                .role("DRIVER")
                .actor(driverId)
                .credentialId(credentialId)
                .build();

        return ResponseEntity.ok(CommonResponse.success(response, "기사 로그인 성공"));
    }
}
