package com.taxidispatcher.services.user.presentation;

import com.taxidispatcher.services.user.application.dto.request.RegisterUserRequest;
import com.taxidispatcher.services.user.application.dto.request.UpdateUserRequest;
import com.taxidispatcher.services.user.application.dto.response.UserProfileResponse;
import com.taxidispatcher.shared.common.config.BaseOpenApiConfig;
import com.taxidispatcher.shared.common.jwt.AuthUser;
import com.taxidispatcher.shared.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 사용자 프로필 API (Swagger 문서)
 * Spring MVC 매핑 어노테이션은 UserController에서 직접 선언
 */
@Tag(name = "User", description = "사용자 프로필 관리")
@SecurityRequirement(name = BaseOpenApiConfig.ACCESS_TOKEN_SCHEME)
public interface UserApi {

    @Operation(summary = "프로필 등록", description = "새로운 사용자 프로필 등록")
    @ApiResponse(responseCode = "201", description = "프로필 등록 성공")
    @ApiResponse(responseCode = "409", description = "이미 등록된 프로필", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<UserProfileResponse>> registerProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody RegisterUserRequest request);

    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필 정보 조회")
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공")
    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal AuthUser authUser);

    @Operation(summary = "프로필 수정", description = "로그인한 사용자의 프로필 정보 수정")
    @ApiResponse(responseCode = "200", description = "프로필 수정 성공")
    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<UserProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UpdateUserRequest request);

    @Operation(summary = "프로필 삭제", description = "로그인한 사용자의 프로필 삭제 (Soft delete)")
    @ApiResponse(responseCode = "204", description = "프로필 삭제 성공")
    @ApiResponse(responseCode = "404", description = "프로필을 찾을 수 없음", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<Void> deleteMyProfile(
            @AuthenticationPrincipal AuthUser authUser);
}
