package com.taxidispatcher.services.account.presentation;

import com.taxidispatcher.services.account.application.dto.request.LoginRequest;
import com.taxidispatcher.services.account.application.dto.request.RegisterRequest;
import com.taxidispatcher.services.account.application.dto.response.LoginResponse;
import com.taxidispatcher.services.account.application.dto.response.RegisterResponse;
import com.taxidispatcher.services.account.application.service.AccountService;
import com.taxidispatcher.services.account.domain.account.Account;
import com.taxidispatcher.shared.common.jwt.JwtException;
import com.taxidispatcher.shared.common.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * 회원가입
     */
    @Override
    @PostMapping("/register")
    public Object register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 1. 비밀번호 일치 확인
            if (!request.isPasswordMatched()) {
                return buildErrorResponse("INVALID_REQUEST", "비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
            }

            // 2. 계정 등록
            Account account = accountService.registerAccount(request.getLoginId(), request.getPassword());

            // 3. 응답 구성
            RegisterResponse response = RegisterResponse.from(account);

            return buildSuccessResponse(response, "회원가입 완료", HttpStatus.OK);

        } catch (Exception e) {
            log.error("회원가입 실패", e);
            return buildErrorResponse("ACCOUNT_DUPLICATE_EMAIL",
                    e.getMessage() != null ? e.getMessage() : "회원가입 실패",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 로그인
     */
    @Override
    @PostMapping("/login")
    public Object login(@Valid @RequestBody LoginRequest request) {
        try {
            // 1. 계정 검증 및 로그인 처리
            Account account = accountService.loginAccount(request.getLoginId(), request.getPassword());

            // 2. JWT 토큰 생성
            // accountId를 Long으로 변환하기 위해 String을 사용하지만,
            // 실제로는 UUID를 숫자로 변환해야 함 (해시값 사용)
            Long accountIdNumeric = account.getAccountId().getValue().hashCode() & 0x7FFFFFFFL;

            String token = jwtTokenProvider.generateToken(
                    accountIdNumeric,
                    request.getUserType(),
                    request.getLoginId()
            );

            // 3. 응답 구성
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .expiresIn(3600L)  // 1시간
                    .accountId(account.getAccountId().getValue())
                    .loginId(request.getLoginId())
                    .build();

            return buildSuccessResponse(response, "로그인 성공", HttpStatus.OK);

        } catch (JwtException je) {
            log.error("JWT 토큰 생성 실패", je);
            return buildErrorResponse(je.getCode(), je.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.error("로그인 실패", e);
            return buildErrorResponse("UNAUTHORIZED",
                    e.getMessage() != null ? e.getMessage() : "로그인 실패",
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 성공 응답 생성
     */
    private ResponseEntity<Map<String, Object>> buildSuccessResponse(Object data, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", "SUCCESS");
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 에러 응답 생성
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String code, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
        return ResponseEntity.status(status).body(response);
    }
}
