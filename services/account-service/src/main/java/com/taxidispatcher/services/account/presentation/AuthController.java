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
     * 로그인
     */
    @Override
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Account account = accountService.loginAccount(request.getLoginId(), request.getPassword());

        Long accountIdNumeric = account.getAccountId().getValue().hashCode() & 0x7FFFFFFFL;
        String token = jwtTokenProvider.generateToken(
                accountIdNumeric,
                request.getUserType(),
                request.getLoginId()
        );

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .expiresIn(3600L)
                .accountId(account.getAccountId().getValue())
                .loginId(request.getLoginId())
                .build();

        return ResponseEntity.ok(CommonResponse.success(response, "로그인 성공"));
    }
}
