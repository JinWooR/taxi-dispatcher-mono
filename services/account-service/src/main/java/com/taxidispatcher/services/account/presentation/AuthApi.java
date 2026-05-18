package com.taxidispatcher.services.account.presentation;

import com.taxidispatcher.services.account.application.dto.request.LoginRequest;
import com.taxidispatcher.services.account.application.dto.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 인증 API 명세 인터페이스 (Swagger 문서 전용)
 * Spring MVC 매핑 어노테이션은 AuthController에서 직접 선언
 */
@Tag(name = "Auth API", description = "인증 관련 API")
public interface AuthApi {

    @Operation(summary = "회원가입", description = "새로운 계정을 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(schema = @Schema(type = "object"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (비밀번호 불일치, 중복된 이메일 등)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류"
            )
    })
    Object register(@Valid @RequestBody RegisterRequest request);

    @Operation(summary = "로그인", description = "계정으로 로그인하여 JWT 토큰을 발급받습니다")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(type = "object"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (필수 파라미터 누락)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (계정 없음, 비밀번호 오류)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류"
            )
    })
    Object login(@Valid @RequestBody LoginRequest request);
}
