package com.taxidispatcher.services.account.presentation;

import com.taxidispatcher.services.account.application.dto.request.LoginRequest;
import com.taxidispatcher.services.account.application.dto.request.RegisterRequest;
import com.taxidispatcher.services.account.application.dto.response.LoginResponse;
import com.taxidispatcher.services.account.application.dto.response.RegisterResponse;
import com.taxidispatcher.services.account.application.dto.response.TokenInfo;
import com.taxidispatcher.services.account.application.service.AccountService;
import com.taxidispatcher.services.account.application.service.AuthService;
import com.taxidispatcher.services.account.domain.account.Account;
import com.taxidispatcher.shared.common.exception.DomainException;
import com.taxidispatcher.shared.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AccountService accountService;
    private final AuthService authService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        Account account = accountService.registerAccount(request.getLoginId(), request.getPassword());
        return ResponseEntity.ok(CommonResponse.success(RegisterResponse.from(account), "회원가입 완료"));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Account account = accountService.loginAccount(request.getLoginId(), request.getPassword());
        String credentialId = extractCredentialId(account, request.getLoginId());
        return ResponseEntity.ok(CommonResponse.success(authService.login(account, "NONE", null, credentialId), "로그인 성공"));
    }

    @Override
    @PostMapping("/login/user")
    public ResponseEntity<CommonResponse<LoginResponse>> loginUser(@Valid @RequestBody LoginRequest request) {
        Account account = accountService.loginAccount(request.getLoginId(), request.getPassword());
        String credentialId = extractCredentialId(account, request.getLoginId());
        return ResponseEntity.ok(CommonResponse.success(authService.loginAsRole(account, "USER", credentialId), "사용자 로그인 성공"));
    }

    @Override
    @PostMapping("/login/driver")
    public ResponseEntity<CommonResponse<LoginResponse>> loginDriver(@Valid @RequestBody LoginRequest request) {
        Account account = accountService.loginAccount(request.getLoginId(), request.getPassword());
        String credentialId = extractCredentialId(account, request.getLoginId());
        return ResponseEntity.ok(CommonResponse.success(authService.loginAsRole(account, "DRIVER", credentialId), "기사 로그인 성공"));
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<TokenInfo>> refresh(@RequestHeader("Authorization") String bearerToken) {
        TokenInfo tokenInfo = authService.refreshAccessToken(extractBearer(bearerToken));
        return ResponseEntity.ok(CommonResponse.success(tokenInfo, "액세스 토큰 재발급 성공"));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(@RequestHeader("Authorization") String bearerToken) {
        authService.logout(extractBearer(bearerToken));
        return ResponseEntity.ok(CommonResponse.success(null, "로그아웃 성공"));
    }

    @Override
    @PostMapping("/upgrade/user")
    public ResponseEntity<CommonResponse<LoginResponse>> upgradeUser(@RequestHeader("Authorization") String bearerToken) {
        LoginResponse response = authService.upgradeRole(extractBearer(bearerToken), "USER");
        return ResponseEntity.ok(CommonResponse.success(response, "사용자 권한 승격 성공"));
    }

    @Override
    @PostMapping("/upgrade/driver")
    public ResponseEntity<CommonResponse<LoginResponse>> upgradeDriver(@RequestHeader("Authorization") String bearerToken) {
        LoginResponse response = authService.upgradeRole(extractBearer(bearerToken), "DRIVER");
        return ResponseEntity.ok(CommonResponse.success(response, "기사 권한 승격 성공"));
    }

    private String extractCredentialId(Account account, String loginId) {
        return account.findBasicCredential(loginId)
                .map(cred -> cred.getCredentialId().getValue())
                .orElse(null);
    }

    private String extractBearer(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new DomainException("INVALID_TOKEN", "유효하지 않은 토큰 형식입니다", HttpStatus.UNAUTHORIZED);
        }
        return bearerToken.substring(7);
    }
}
