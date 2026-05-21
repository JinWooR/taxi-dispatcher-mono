package com.taxidispatcher.services.account.presentation;

import com.taxidispatcher.services.account.application.dto.request.LoginRequest;
import com.taxidispatcher.services.account.application.dto.request.RegisterRequest;
import com.taxidispatcher.services.account.application.dto.response.LoginResponse;
import com.taxidispatcher.services.account.application.dto.response.RegisterResponse;
import com.taxidispatcher.shared.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
                    description = "잘못된 요청 (필수 파라미터 누락, 형식 오류 등)"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 이메일"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류"
            )
    })
    ResponseEntity<CommonResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request);

    @Operation(summary = "기본 로그인", description = "계정 인증 후 기본 토큰을 발급받습니다. 프로필 등록 전 단계에서 사용합니다")
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
    ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "사용자 로그인", description = "사용자 권한으로 로그인하여 JWT 토큰을 발급받습니다")
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
    ResponseEntity<CommonResponse<LoginResponse>> loginUser(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "기사 로그인", description = "기사 권한으로 로그인하여 JWT 토큰을 발급받습니다")
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
    ResponseEntity<CommonResponse<LoginResponse>> loginDriver(@Valid @RequestBody LoginRequest request);
}
