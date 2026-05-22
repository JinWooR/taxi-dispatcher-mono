package com.taxidispatcher.services.account.presentation;

import com.taxidispatcher.services.account.application.dto.request.LoginRequest;
import com.taxidispatcher.services.account.application.dto.request.RegisterRequest;
import com.taxidispatcher.services.account.application.dto.response.LoginResponse;
import com.taxidispatcher.services.account.application.dto.response.RegisterResponse;
import com.taxidispatcher.services.account.application.dto.response.TokenInfo;
import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import com.taxidispatcher.shared.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Auth API", description = "인증 관련 API")
public interface AuthApi {

    @Operation(summary = "회원가입", description = "새로운 계정을 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "중복된 이메일"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request);

    @Operation(summary = "기본 로그인", description = "프로필 등록 전 계정 인증 후 토큰을 발급받습니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "사용자 로그인", description = "사용자 권한으로 로그인하여 토큰을 발급받습니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<LoginResponse>> loginUser(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "기사 로그인", description = "기사 권한으로 로그인하여 토큰을 발급받습니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<LoginResponse>> loginDriver(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "액세스 토큰 재발급", description = "Refresh Token으로 새로운 Access Token을 발급받습니다")
    @SecurityRequirement(name = BaseOpenApiConfig.REFRESH_TOKEN_SCHEME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<TokenInfo>> refresh(@RequestHeader("Authorization") String bearerToken);

    @Operation(summary = "로그아웃", description = "Refresh Token을 삭제하여 로그아웃합니다")
    @SecurityRequirement(name = BaseOpenApiConfig.REFRESH_TOKEN_SCHEME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<CommonResponse<Void>> logout(@RequestHeader("Authorization") String bearerToken);
}
